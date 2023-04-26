package com.bachlinh.order.core.entity.context;

import com.bachlinh.order.core.entity.model.BaseEntity;
import com.bachlinh.order.core.entity.trigger.spi.EntityTrigger;
import com.bachlinh.order.core.entity.validator.spi.EntityValidator;

import java.util.Collection;
import java.util.Collections;

/**
 * The context for hold the entity, {@link EntityValidator} for validate the entity,
 * caching region for define the cache storage name.
 *
 * @author Hoang Minh Quan
 */
public interface EntityContext extends IdContext, SearchManager {

    /**
     * Return the entity associate with its context.
     *
     * @return The entity
     */
    BaseEntity getEntity();

    /**
     * Return the validator of entity.
     *
     * @return collection of entity validators
     */
    Collection<EntityValidator<?>> getValidators();

    Collection<EntityTrigger<?>> getTrigger();

    Collection<String> search(String keyword);

    @Override
    default Collection<String> search(Class<?> entity, String keyword) {
        Class<?> entityClass = getEntity().getClass();
        if (entity.equals(entityClass)) {
            return search(keyword);
        }
        return Collections.emptyList();
    }

    /**
     * Return the cache region of entity
     *
     * @return cache region
     */
    String getCacheRegion();
}