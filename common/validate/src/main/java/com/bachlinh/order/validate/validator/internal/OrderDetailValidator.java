package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

@ActiveReflection
public class OrderDetailValidator extends AbstractValidator<OrderDetail> {

    @ActiveReflection
    public OrderDetailValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        // Do nothing
    }

    @Override
    protected ValidateResult doValidate(OrderDetail entity) {
        Result result = new Result();
        if (entity.getAmount() <= 0) {
            result.addMessageError("Order amount: must be a positive number");
        }
        return result;
    }
}
