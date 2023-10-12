package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

import java.text.MessageFormat;

import org.springframework.util.StringUtils;

@ActiveReflection
@ApplyOn(entity = EmailTemplateFolder.class)
public class EmailTemplateFolderValidator extends AbstractValidator<EmailTemplateFolder> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";

    private EmailTemplateFolderRepository emailTemplateFolderRepository;
    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (emailTemplateFolderRepository == null) {
            emailTemplateFolderRepository = resolveRepository(EmailTemplateFolderRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(EmailTemplateFolder entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);
        MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);

        String emailTemplateFolderName = "Name of email template folder";
        if (!StringUtils.hasText(entity.getName())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), emailTemplateFolderName));
        } else {
            if (entity.getName().length() > 300) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), emailTemplateFolderName, "300"));
            }
            EmailTemplateFolder old = emailTemplateFolderRepository.getEmailTemplateFolderForUpdate(entity.getId());
            if (!old.getName().equals(entity.getName()) && emailTemplateFolderRepository.isEmailTemplateFolderNameExisted(entity.getName())) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), emailTemplateFolderName));
            }
        }
        return result;
    }
}
