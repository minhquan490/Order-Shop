package com.bachlinh.order.validator.spi;

import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.model.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

/**
 * Base validator for all entities validator use in this project.
 *
 * @author Hoang Minh Quan
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractValidator<T extends BaseEntity> implements EntityValidator<T> {
    private ApplicationContext context;

    protected ApplicationContext getApplicationContext() {
        return context;
    }
}
