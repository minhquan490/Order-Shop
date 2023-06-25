package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

@ActiveReflection
public class AddressValidator extends AbstractValidator<Address> {

    @ActiveReflection
    public AddressValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        // Do nothing
    }

    @Override
    protected ValidateResult doValidate(Address entity) {
        Result result = new Result();
        if (entity.getValue().isBlank()) {
            result.addMessageError("Detail address: is blank");
        }
        if (entity.getCity().isBlank()) {
            result.addMessageError("City: is blank");
        }
        if (entity.getCountry().isBlank()) {
            result.addMessageError("Country: is blank");
        }
        return result;
    }
}
