package com.bachlinh.order.web.common.entity;

import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.EntityMapperHolder;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.repository.query.AbstractQueryMetadataContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class DefaultEntityContext extends AbstractQueryMetadataContext implements EntityMapperHolder {
    private final List<EntityValidator<? extends BaseEntity<?>>> validators;
    private final List<EntityTrigger<? extends BaseEntity<?>>> triggers;
    private final DependenciesResolver dependenciesResolver;
    private EntityMapperFactory entityMapperFactory;

    public DefaultEntityContext(Class<?> entity, DependenciesResolver dependenciesResolver, SearchManager searchManager, Environment environment) {
        super(entity, searchManager);
        Logger log = LoggerFactory.getLogger(getClass());
        try {
            if (log.isDebugEnabled()) {
                log.debug("Init entity context for entity {}", entity.getSimpleName());
            }
            this.validators = getValidators(entity, dependenciesResolver);
            this.triggers = getTriggers(entity, dependenciesResolver, environment);
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
    public Collection<EntityValidator<?>> getValidators() {
        return new ArrayList<>(validators);
    }

    @Override
    public Collection<EntityTrigger<?>> getTrigger() {
        return new ArrayList<>(triggers);
    }

    @Override
    public <T extends BaseEntity<?>> EntityMapper<T> getMapper(Class<T> entityType) {
        if (entityMapperFactory == null) {
            entityMapperFactory = dependenciesResolver.resolveDependencies(EntityMapperFactory.class);
        }
        return entityMapperFactory.createMapper(entityType);
    }

    @Override
    protected DependenciesResolver getResolver() {
        return this.dependenciesResolver;
    }

    @Override
    protected Initializer<BaseEntity<?>> getEntityInitializer() {
        return new EntityInitializer();
    }

    @Override
    protected Initializer<FormulaProcessor> getFormulaInitializer() {
        return new FormulaInitializer();
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
                    Initializer<EntityValidator<?>> validatorInitializer = new EntityValidatorInitializer();
                    return validatorInitializer.getObject(validator, resolver);
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
                    return (applyOn.entity().equals(entity) && applyOn.type().equals(ApplyOn.ApplyType.SINGULAR)) || applyOn.type().equals(ApplyOn.ApplyType.ALL);
                })
                .map(clazz -> (Class<T>) clazz)
                .map(triggerClass -> initTrigger(triggerClass, dependenciesResolver, environment))
                .sorted(Comparator.comparing(entityTrigger -> {
                    ApplyOn applyOn = entityTrigger.getClass().getAnnotation(ApplyOn.class);
                    return applyOn.order();
                }))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private <T extends EntityTrigger<? extends BaseEntity<?>>> T initTrigger(Class<T> triggerClass, DependenciesResolver dependenciesResolver, Environment environment) {
        Initializer<EntityTrigger<? extends BaseEntity<?>>> initializer = new TriggerInitializer();
        return (T) initializer.getObject(triggerClass, environment, dependenciesResolver);
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
