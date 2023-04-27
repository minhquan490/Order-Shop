package com.bachlinh.order.core.entity.validator.spi;

import com.bachlinh.order.core.entity.model.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

/**
 * Base validator for all entities validator use in this project.
 *
 * @author Hoang Minh Quan
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract non-sealed class AbstractValidator<T extends BaseEntity> implements EntityValidator<T> {
    private ApplicationContext context;

    protected ApplicationContext getApplicationContext() {
        return context;
    }
}
