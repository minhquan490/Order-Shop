package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
public class EmailFoldersValidator extends AbstractValidator<EmailFolders> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String EXISTED_ID = "MSG-000007";

    private EmailFoldersRepository emailFoldersRepository;
    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public EmailFoldersValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (emailFoldersRepository == null) {
            emailFoldersRepository = getResolver().resolveDependencies(EmailFoldersRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(EmailFolders entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getName())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Name of email folder"));
        } else {
            if (entity.getName().length() > 300) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Name of email folder"));
            }
        }

        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (emailFoldersRepository.isFolderExisted(entity.getName(), customer)) {
            result.addMessageError(MessageFormat.format(existedMessage.getValue(), "Email folder name"));
        }
        return result;
    }
}
