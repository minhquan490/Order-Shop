package com.bachlinh.order.entity;

import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.transaction.spi.EntityTransactionManager;

import java.io.IOException;

/**
 * The factory for create entity. All entities much be annotated with
 * {@link jakarta.persistence.Entity} for scanner can find it and has
 * a package private constructor.
 *
 * @author Hoang Minh Quan
 */
public interface EntityFactory {

    /**
     * Check the entity can be created by this factory.
     *
     * @return true, if the entity can be created.
     */
    <T> boolean containsEntity(Class<T> entityType);

    /**
     * Create entity and return it for use.
     *
     * @param entityType type of entity.
     * @return the entity with type.
     */
    <T> T getEntity(Class<T> entityType);

    /**
     * Return the entity context associate with entity.
     *
     * @param entityType type of entity.
     * @return the context associate with entity.
     */
    EntityContext getEntityContext(Class<?> entityType);

    EntityTransactionManager<?> getTransactionManager();

    DependenciesResolver getResolver();

    String activeProfile();

    /**
     * The builder for build the entity factory.
     *
     * @author Hoang Minh Quan.
     */
    interface EntityFactoryBuilder {

        EntityFactoryBuilder profile(String profile);

        EntityFactoryBuilder container(ContainerWrapper wrapper);

        /**
         * Build the entity factory.
         *
         * @return Built entity factory.
         */
        EntityFactory build() throws IOException;
    }
}
