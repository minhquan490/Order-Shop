package com.bachlinh.order.core.entity.validator.spi;

import com.bachlinh.order.core.entity.model.BaseEntity;

public interface EntityValidator<T extends BaseEntity> {
    /**
     * Validate the entity before save into a database.
     *
     * @param entity for validation.
     * @return result after validate entity.
     */
    ValidateResult validate(T entity);
}
