package com.bachlinh.order.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validator.spi.AbstractValidator;
import com.bachlinh.order.validator.spi.Result;

@ActiveReflection
public class OrderValidator extends AbstractValidator<Order> {

    @ActiveReflection
    public OrderValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        // Do nothing
    }

    @Override
    protected ValidateResult doValidate(Order entity) {
        Result result = new Result();
        if (entity.getTotalDeposit() < 0) {
            result.addMessageError("Total deposit: must greater or equal 0");
        }
        if (entity.getTimeOrder() == null) {
            result.addMessageError("Time order: must be specify");
        }
        return result;
    }
}
