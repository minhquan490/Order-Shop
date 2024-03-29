package com.bachlinh.order.web.common.validator;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import com.bachlinh.order.web.repository.spi.EmailTemplateRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;

import java.text.MessageFormat;

import org.springframework.util.StringUtils;

@ActiveReflection
@ApplyOn(entity = EmailTemplate.class)
public class EmailTemplateValidator extends AbstractValidator<EmailTemplate> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";

    private EmailTemplateRepository emailTemplateRepository;
    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (emailTemplateRepository == null) {
            emailTemplateRepository = resolveRepository(EmailTemplateRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(EmailTemplate entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getContent())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Content of email template"));
        } else {
            if (entity.getContent().length() > 700) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Content of email template", "700"));
            }
        }

        if (!StringUtils.hasText(entity.getTitle())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Title of email template"));
        } else {
            if (entity.getTitle().length() > 255) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Title", "255"));
            }
        }

        if (StringUtils.hasText(entity.getName()) && entity.getName().length() > 100) {
            result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Name", "100"));
        }

        return result;
    }
}
