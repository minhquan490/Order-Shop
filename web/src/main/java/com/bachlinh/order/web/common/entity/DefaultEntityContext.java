package com.bachlinh.order.web.common.entity;

import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.exception.system.common.NoTransactionException;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.apache.lucene.store.Directory;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefaultEntityContext implements EntityContext {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Class<?> idType;
    private final BaseEntity<?> baseEntity;
    private final String prefix;
    private final String cacheRegion;
    private final List<EntityValidator<? extends BaseEntity<?>>> validators;
    private final List<EntityTrigger<? extends BaseEntity<?>>> triggers;
    private final SearchManager searchManager;
    private volatile Integer previousId;
    private volatile int createIdTime = -1;
    private final Class<?> entityType;
    private final DependenciesResolver dependenciesResolver;

    public DefaultEntityContext(Class<?> entity, DependenciesResolver dependenciesResolver, SearchManager searchManager, Environment environment) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Init entity context for entity {}", entity.getSimpleName());
            }
            this.idType = queryIdType(entity);
            this.baseEntity = (BaseEntity<?>) newInstance(entity, null, null);
            this.validators = getValidators(entity, dependenciesResolver);
            this.triggers = getTriggers(entity, dependenciesResolver, environment);
            Label label = entity.getAnnotation(Label.class);
            if (label != null) {
                this.prefix = entity.getAnnotation(Label.class).value();
            } else {
                this.prefix = null;
            }
            Cache cache = entity.getAnnotation(Cache.class);
            if (cache != null) {
                this.cacheRegion = cache.region();
            } else {
                this.cacheRegion = null;
            }
            this.searchManager = searchManager;
            this.entityType = entity;
            this.dependenciesResolver = dependenciesResolver;
        } catch (Exception e) {
            throw new PersistenceException("Can not instance entity with type [" + entity.getSimpleName() + "]", e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Init complete");
            }
        }
    }

    @Override
    public BaseEntity<?> getEntity() {
        return (BaseEntity<?>) ((AbstractEntity<?>) baseEntity).clone();
    }

    @Override
    public Collection<EntityValidator<?>> getValidators() {
        return new ArrayList<>(validators);
    }

    @Override
    public Collection<EntityTrigger<?>> getTrigger() {
        return new ArrayList<>(triggers);
    }

    @Override
    public Collection<String> search(String keyword) {
        return searchManager.search(getEntity().getClass(), keyword);
    }

    @Override
    public String getCacheRegion() {
        return cacheRegion;
    }

    @Override
    public synchronized Object getNextId() {
        if (this.previousId == null) {
            try {
                this.previousId = configLastId();
            } catch (Exception e) {
                throw new PersistenceException(String.format("Can not get next id of entity [%s]", entityType.getName()));
            }
        }
        if (this.createIdTime < 0) {
            throw new NoTransactionException("You must call beginTransaction() before this method");
        }
        this.previousId += 1;
        this.createIdTime += 1;
        if (prefix == null && (idType.equals(int.class) || idType.equals(Integer.class))) {
            return previousId;
        }
        return prefix + String.format("%06d", previousId);
    }

    @Override
    public void beginTransaction() {
        this.createIdTime = 0;
    }

    @Override
    public void commit() {
        this.createIdTime = -1;
    }

    @Override
    public synchronized void rollback() {
        if (this.createIdTime > 0) {
            this.previousId -= createIdTime;
            this.createIdTime = -1;
        }
    }

    @Override
    public Directory getDirectory(Class<?> entity) {
        return searchManager.getDirectory(entity);
    }

    @Override
    public void analyze(Object entity) {
        searchManager.analyze(entity);
    }

    @Override
    public void analyze(Collection<Object> entities) {
        searchManager.analyze(entities);
    }

    @SuppressWarnings("unchecked")
    private <T extends EntityValidator<? extends BaseEntity<?>>> List<T> getValidators(Class<?> entity, DependenciesResolver resolver) {
        ApplicationScanner scanner = new ApplicationScanner();
        return scanner.findComponents()
                .stream()
                .filter(this::isValidator)
                .filter(validatorClass -> {
                    ApplyOn applyOn = validatorClass.getAnnotation(ApplyOn.class);
                    return applyOn.entity().equals(entity);
                })
                .map(validator -> {
                    Object returnValidator = newInstance(validator, new Class[]{DependenciesResolver.class}, new Object[]{resolver});
                    if (log.isDebugEnabled()) {
                        log.debug("Init validator [{}] for entity [{}]", validator.getName(), baseEntity.getClass().getName());
                    }
                    return returnValidator;
                })
                .map(returnObject -> (T) returnObject)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private <T extends EntityTrigger<? extends BaseEntity<?>>> List<T> getTriggers(Class<?> entity, DependenciesResolver dependenciesResolver, Environment environment) {
        ApplicationScanner scanner = new ApplicationScanner();
        return scanner.findComponents()
                .stream()
                .filter(this::isTrigger)
                .filter(triggerClass -> {
                    ApplyOn applyOn = triggerClass.getAnnotation(ApplyOn.class);
                    return applyOn.entity().equals(entity) || applyOn.type().equals(ApplyOn.ApplyType.ALL);
                })
                .map(clazz -> (Class<T>) clazz)
                .map(triggerClass -> initTrigger(triggerClass, dependenciesResolver, environment))
                .sorted(Comparator.comparing(entityTrigger -> {
                    ApplyOn applyOn = entityTrigger.getClass().getAnnotation(ApplyOn.class);
                    return applyOn.order();
                }))
                .toList();
    }

    private Class<?> queryIdType(Class<?> entityType) {
        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field.getType();
            }
        }
        throw new PersistenceException("Can not find type of entity id");
    }

    private Object newInstance(Class<?> initiator, @Nullable Class<?>[] paramTypes, @Nullable Object[] params) {
        try {
            Constructor<?> constructor;
            if (paramTypes == null) {
                constructor = initiator.getDeclaredConstructor();
            } else {
                constructor = initiator.getDeclaredConstructor(paramTypes);
            }
            if (!Modifier.isPublic(constructor.getModifiers())) {
                constructor.setAccessible(true);
            }
            if (params == null) {
                return constructor.newInstance();
            } else {
                return constructor.newInstance(params);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new CriticalException("Can not init validator [" + initiator.getName() + "]");
        }
    }

    private <T extends EntityTrigger<? extends BaseEntity<?>>> T initTrigger(Class<T> triggerClass, DependenciesResolver dependenciesResolver, Environment environment) {
        try {
            Constructor<T> constructor = triggerClass.getDeclaredConstructor(DependenciesResolver.class);
            if (!Modifier.isPublic(constructor.getModifiers())) {
                constructor.setAccessible(true);
            }
            AbstractTrigger<? extends BaseEntity<?>> trigger = (AbstractTrigger<? extends BaseEntity<?>>) constructor.newInstance(dependenciesResolver);
            trigger.setEnvironment(environment);

            return triggerClass.cast(trigger);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new CriticalException("Can not init trigger [" + triggerClass.getName() + "]");
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Init trigger [{}] for entity [{}]", triggerClass.getName(), baseEntity.getClass().getName());
            }
        }
    }

    private int configLastId() throws ClassNotFoundException {
        String repositoryPattern = "com.bachlinh.order.repository.{0}Repository";
        String repositoryName = MessageFormat.format(repositoryPattern, entityType.getSimpleName());
        Class<?> repositoryClass = Class.forName(repositoryName);
        AbstractRepository<?, ?> repository = (AbstractRepository<?, ?>) dependenciesResolver.resolveDependencies(repositoryClass);
        String sql = MessageFormat.format("SELECT MAX(ID) FROM {0}", entityType.getAnnotation(Table.class).name());
        List<?> result = repository.executeNativeQuery(sql, Collections.emptyMap(), idType);
        if (result.isEmpty()) {
            return 0;
        }
        if (result.get(0) instanceof String idString) {
            String suffixId = idString.split("-")[1];
            return Integer.parseInt(suffixId);
        }
        if (result.get(0) instanceof Integer idInt) {
            return idInt;
        }
        return 0;
    }

    private boolean isTrigger(Class<?> clazz) {
        return clazz.isAnnotationPresent(ApplyOn.class) &&
                EntityTrigger.class.isAssignableFrom(clazz);
    }

    private boolean isValidator(Class<?> clazz) {
        return clazz.isAnnotationPresent(ApplyOn.class) &&
                EntityValidator.class.isAssignableFrom(clazz);
    }
}
