package com.bachlinh.order.entity.context.internal;

import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.store.Directory;
import org.hibernate.annotations.Cache;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import com.bachlinh.order.annotation.Ignore;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Trigger;
import com.bachlinh.order.annotation.Validator;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.exception.system.common.NoTransactionException;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DefaultEntityContext implements EntityContext {

    private final Class<?> idType;
    private final BaseEntity baseEntity;
    private final String prefix;
    private final String cacheRegion;
    private final Collection<EntityValidator<? extends BaseEntity>> validators;
    private final Collection<EntityTrigger<? extends BaseEntity>> triggers;
    private final SearchManager searchManager;
    private volatile int previousId;
    private volatile int createIdTime = -1;

    public DefaultEntityContext(Class<?> entity, DependenciesResolver dependenciesResolver, SearchManager searchManager) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Init entity context for entity {}", entity.getSimpleName());
            }
            this.idType = queryIdType(entity);
            this.baseEntity = (BaseEntity) newInstance(entity, null, null);
            this.validators = getValidators(entity, dependenciesResolver);
            this.triggers = getTriggers(entity, dependenciesResolver);
            this.previousId = 0;
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
            if (log.isDebugEnabled()) {
                log.debug("Init complete");
            }
        } catch (Exception e) {
            throw new PersistenceException("Can not instance entity with type [" + entity.getSimpleName() + "]", e);
        }
        this.searchManager = searchManager;
    }

    @Override
    public BaseEntity getEntity() {
        return (BaseEntity) ((AbstractEntity) baseEntity).clone();
    }

    @Override
    public Collection<EntityValidator<?>> getValidators() {
        return validators;
    }

    @Override
    public Collection<EntityTrigger<?>> getTrigger() {
        return triggers;
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
        this.previousId -= createIdTime;
        this.createIdTime = -1;
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

    private List<EntityValidator<? extends BaseEntity>> getValidators(Class<?> entity, DependenciesResolver resolver) {
        Validator v = entity.getAnnotation(Validator.class);
        if (v == null) {
            return Collections.emptyList();
        }
        List<EntityValidator<?>> vs = new ArrayList<>();
        for (String validatorName : v.validators()) {
            try {
                Class<?> validator = Class.forName(validatorName);
                if (log.isDebugEnabled()) {
                    log.debug("Create validator [{}]", validator.getName());
                }
                EntityValidator<? extends BaseEntity> entityValidator = (EntityValidator<? extends BaseEntity>) newInstance(validator, new Class[]{DependenciesResolver.class}, new Object[]{resolver});
                vs.add(entityValidator);
            } catch (Exception e) {
                log.error("Can not create validator because [{}]", e.getClass().getName(), e);
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("Init validator [{}] for class [{}]", validatorName, baseEntity.getClass().getName());
                }
            }
        }
        return vs;
    }

    private List<EntityTrigger<? extends BaseEntity>> getTriggers(Class<?> entity, DependenciesResolver dependenciesResolver) {
        List<EntityTrigger<? extends BaseEntity>> entityTriggers = new ArrayList<>();
        entityTriggers.add(initTrigger("com.bachlinh.order.trigger.spi.IdGenTrigger", dependenciesResolver));
        entityTriggers.add(initTrigger("com.bachlinh.order.trigger.spi.AuditingTrigger", dependenciesResolver));
        Trigger trigger = entity.getAnnotation(Trigger.class);
        if (trigger == null) {
            return entityTriggers;
        }
        try {
            for (String triggerName : trigger.triggers()) {
                EntityTrigger<? extends BaseEntity> triggerObject = initTrigger(triggerName, dependenciesResolver);
                if (triggerObject != null) {
                    entityTriggers.add(triggerObject);
                }
            }
        } catch (Exception e) {
            log.error("Can not create trigger", e);
        }
        return entityTriggers.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(entityTrigger -> {
                    Order order = entityTrigger.getClass().getAnnotation(Order.class);
                    if (order == null) {
                        return Ordered.LOWEST_PRECEDENCE;
                    }
                    return order.value();
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

    private Object newInstance(Class<?> initiator, @Nullable Class<?>[] paramTypes, @Nullable Object[] params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
    }

    private EntityTrigger<? extends BaseEntity> initTrigger(String triggerName, DependenciesResolver dependenciesResolver) {
        try {
            Class<?> triggerClass = Class.forName(triggerName);
            if (triggerClass.isAnnotationPresent(Ignore.class)) {
                return null;
            }
            Constructor<?> constructor = triggerClass.getDeclaredConstructor(DependenciesResolver.class);
            if (!Modifier.isPublic(constructor.getModifiers())) {
                constructor.setAccessible(true);
            }
            return (EntityTrigger<? extends BaseEntity>) constructor.newInstance(dependenciesResolver);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new CriticalException("Can not init trigger [" + triggerName + "]");
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Init trigger [{}] for entity [{}]", triggerName, baseEntity.getClass().getName());
            }
        }
    }
}
