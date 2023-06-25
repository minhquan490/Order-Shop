package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

@ActiveReflection
public class CategoryValidator extends AbstractValidator<Category> {

    @ActiveReflection
    public CategoryValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        // Do nothing
    }

    @Override
    protected ValidateResult doValidate(Category entity) {
        Result result = new Result();
        if (entity.getName().length() > 60) {
            result.addMessageError("Category name: is greater than 60");
        }
        if (entity.getName().isBlank()) {
            result.addMessageError("Category name: is blank");
        }
        return result;
    }
}
