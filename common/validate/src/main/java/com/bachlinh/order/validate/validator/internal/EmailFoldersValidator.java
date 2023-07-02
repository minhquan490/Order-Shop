package com.bachlinh.order.validate.validator.internal;

import org.springframework.security.core.context.SecurityContextHolder;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

@ActiveReflection
public class EmailFoldersValidator extends AbstractValidator<EmailFolders> {
    private EmailFoldersRepository emailFoldersRepository;

    @ActiveReflection
    public EmailFoldersValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (emailFoldersRepository == null) {
            emailFoldersRepository = getResolver().resolveDependencies(EmailFoldersRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(EmailFolders entity) {
        Result result = new Result();
        if (entity.getName() == null || entity.getName().isBlank()) {
            result.addMessageError("Email folder name: Name of email folder must not be empty");
        }
        if (entity.getName().length() > 300) {
            result.addMessageError("Email folder name: Name of email folder must not be greater than 300 character");
        }
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (emailFoldersRepository.isFolderExisted(entity.getName(), customer)) {
            result.addMessageError("Email folder name: Email folder name is existed");
        }
        return result;
    }
}
