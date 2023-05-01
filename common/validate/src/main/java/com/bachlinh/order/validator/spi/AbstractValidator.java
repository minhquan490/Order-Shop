package com.bachlinh.order.validator.spi;

import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.service.container.DependenciesResolver;

/**
 * Base validator for all entities validator use in this project.
 *
 * @author Hoang Minh Quan
 */
public abstract class AbstractValidator<T extends BaseEntity> implements EntityValidator<T> {
    private final DependenciesResolver resolver;

    protected AbstractValidator(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    protected DependenciesResolver getResolver() {
        return resolver;
    }
}
