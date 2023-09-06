package com.bachlinh.order.entity;

import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.service.container.DependenciesResolver;

public interface EntityValidator<T extends BaseEntity<?>> {
    /**
     * Validate the entity before save into a database.
     *
     * @param entity for validation.
     * @return result after validate entity.
     */
    ValidateResult validate(T entity);

    void setResolver(DependenciesResolver resolver);
}
