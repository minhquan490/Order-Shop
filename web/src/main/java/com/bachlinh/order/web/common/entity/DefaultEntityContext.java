package com.bachlinh.order.web.common.entity;

import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.EntityMapperHolder;
import com.bachlinh.order.entity.context.EntityIdProvider;
import com.bachlinh.order.trigger.EntityTriggerManager;
import com.bachlinh.order.trigger.EntityTriggerManagerHolder;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.repository.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.repository.query.AbstractQueryMetadataContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultEntityContext extends AbstractQueryMetadataContext implements EntityMapperHolder, EntityTriggerManagerHolder {
    private final List<EntityValidator<? extends BaseEntity<?>>> validators;
    private final DependenciesResolver dependenciesResolver;
    private final EntityTriggerManager entityTriggerManager;
    private EntityMapperFactory entityMapperFactory;

    public DefaultEntityContext(Class<?> entity, DependenciesResolver dependenciesResolver, SearchManager searchManager) {
        super(entity, searchManager);
        Logger log = LoggerFactory.getLogger(getClass());
        try {
            if (log.isDebugEnabled()) {
                log.debug("Init entity context for entity {}", entity.getSimpleName());
            }
            this.validators = getValidators(entity, dependenciesResolver);
            this.dependenciesResolver = dependenciesResolver;
            this.entityTriggerManager = dependenciesResolver.resolveDependencies(EntityTriggerManager.class);
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
    public <T extends BaseEntity<?>> EntityMapper<T> getMapper(Class<T> entityType) {
        if (entityMapperFactory == null) {
            entityMapperFactory = dependenciesResolver.resolveDependencies(EntityMapperFactory.class);
        }
        return entityMapperFactory.createMapper(entityType);
    }

    @Override
    public EntityTriggerManager getEntityTriggerManager() {
        return this.entityTriggerManager;
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
    protected EntityIdProvider getEntityIdProvider(DependenciesResolver resolver, Class<?> entityType, Class<?> idType) {
        return new DefaultEntityIdProvider(resolver, entityType, idType);
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

    private boolean isValidator(Class<?> clazz) {
        return clazz.isAnnotationPresent(ApplyOn.class) &&
                EntityValidator.class.isAssignableFrom(clazz);
    }
}
