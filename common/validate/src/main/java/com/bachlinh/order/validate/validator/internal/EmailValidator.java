package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = Email.class)
public class EmailValidator extends AbstractValidator<Email> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Email entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getContent())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Content"));
        }

        if (!StringUtils.hasText(entity.getTitle())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Title of email"));
        } else {
            if (entity.getTitle().length() > 400) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Length title", "400"));
            }
        }
        return result;
    }
}
