package com.bachlinh.order.entity.context;

import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.model.BaseEntity;

import java.util.Collection;
import java.util.Collections;

/**
 * The context for hold the entity, {@link EntityValidator} for validate the entity,
 * caching region for define the cache storage name.
 *
 * @author Hoang Minh Quan
 */
public interface EntityContext extends IdContext, SearchManager, TableMetadataHolder {

    /**
     * Return the entity associate with its context.
     *
     * @return The entity
     */
    BaseEntity<?> getEntity();

    /**
     * Return the validator of entity.
     *
     * @return collection of entity validators
     */
    Collection<EntityValidator<?>> getValidators();

    Collection<String> search(String keyword);

    @Override
    default Collection<String> search(Class<?> entity, String keyword) {
        Class<?> entityClass = getEntity().getClass();
        if (entity.equals(entityClass)) {
            return search(keyword);
        }
        return Collections.emptyList();
    }
}