package com.bachlinh.order.web.common.validator;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import com.bachlinh.order.web.repository.spi.EmailFoldersRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;

import java.text.MessageFormat;

import org.springframework.util.StringUtils;

@ActiveReflection
@ApplyOn(entity = EmailFolders.class)
public class EmailFoldersValidator extends AbstractValidator<EmailFolders> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";

    private EmailFoldersRepository emailFoldersRepository;
    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (emailFoldersRepository == null) {
            emailFoldersRepository = resolveRepository(EmailFoldersRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(EmailFolders entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getName())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Name of email folder"));
        } else {
            if (entity.getName().length() > 300) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Name of email folder"));
            }
        }

        return result;
    }
}
