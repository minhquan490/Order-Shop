package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

@ActiveReflection
public class OrderStatusValidator extends AbstractValidator<OrderStatus> {

    @ActiveReflection
    public OrderStatusValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        // Do nothing
    }

    @Override
    protected ValidateResult doValidate(OrderStatus entity) {
        Result result = new Result();
        if (entity.getStatus().isBlank()) {
            result.addMessageError("Order status: is blank");
        }
        return result;
    }
}
