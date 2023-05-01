package com.bachlinh.order.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validator.spi.AbstractValidator;
import com.bachlinh.order.validator.spi.Result;

@ActiveReflection
public class EmailTemplateFolderValidator extends AbstractValidator<EmailTemplateFolder> {
    private EmailTemplateFolderRepository emailTemplateFolderRepository;

    @ActiveReflection
    protected EmailTemplateFolderValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (emailTemplateFolderRepository == null) {
            emailTemplateFolderRepository = getResolver().resolveDependencies(EmailTemplateFolderRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(EmailTemplateFolder entity) {
        Result result = new Result();
        if (entity.getName() == null || entity.getName().isBlank()) {
            result.addMessageError("Email template folder name: Name of email template folder must not be empty");
        }
        if (entity.getName().length() > 300) {
            result.addMessageError("Email template folder name: Name of email template folder must not be greater than 300 character");
        }
        if (emailTemplateFolderRepository.isEmailTemplateFolderExisted(entity.getName())) {
            result.addMessageError("Email template folder name: Name of email template folder is existed");
        }
        return result;
    }
}
