package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

@ActiveReflection
public class EmailValidator extends AbstractValidator<Email> {

    @ActiveReflection
    public EmailValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        //Do nothing
    }

    @Override
    protected ValidateResult doValidate(Email entity) {
        Result result = new Result();
        if (entity.getContent() == null || entity.getContent().isBlank()) {
            result.addMessageError("Email content: Content must not be empty");
        }
        if (entity.getTitle() == null || entity.getTitle().isBlank()) {
            result.addMessageError("Email title: Title of email must not be empty");
        }
        if (entity.getTitle().length() > 400) {
            result.addMessageError("Email title: Length title must greater than 400 character");
        }
        return result;
    }
}
