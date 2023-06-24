package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

@ActiveReflection
public class EmailTemplateValidator extends AbstractValidator<EmailTemplate> {
    private EmailTemplateRepository emailTemplateRepository;

    @ActiveReflection
    public EmailTemplateValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (emailTemplateRepository == null) {
            emailTemplateRepository = getResolver().resolveDependencies(EmailTemplateRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(EmailTemplate entity) {
        Result result = new Result();
        if (entity.getContent() == null || entity.getContent().isBlank()) {
            result.addMessageError("Email template content: Content of email template must not be empty");
        }
        if (entity.getTitle() == null || entity.getTitle().isBlank()) {
            result.addMessageError("Email template title: Title of email template must not be empty");
        }
        if (entity.getContent().length() > 700) {
            result.addMessageError("Email template content: Content of email template must be greater than 700 character");
        }
        if (entity.getTitle().length() > 255) {
            result.addMessageError("Email template title: Title must be greater than 255 character");
        }
        if (emailTemplateRepository.isEmailTemplateTitleExisted(entity.getTitle())) {
            result.addMessageError("Email template title: Title is existed");
        }
        if (entity.getName().length() > 100) {
            result.addMessageError("Email template name: Name must be less or equal 100 character");
        }
        return result;
    }
}
