package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = MessageSetting.class)
public class MessageSettingValidator extends AbstractValidator<MessageSetting> {
    private static final String LENGTH_MESSAGE_ID = "MSG-000002";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(MessageSetting entity) {
        Result result = new Result();

        if (entity.isNew()) {
            validateMessageValue(entity.getValue(), result);
            validateMessageValueLength(entity.getValue(), result);
        } else {
            MessageSetting old = messageSettingRepository.getMessageById(entity.getId());
            if (!old.getValue().equals(entity.getValue())) {
                validateMessageValue(entity.getValue(), result);
                validateMessageValueLength(entity.getValue(), result);
            }
        }

        return result;
    }

    private void validateMessageValue(String value, ValidateResult result) {
        if (messageSettingRepository.messageValueExisted(value)) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Message oldValue");
            result.addMessageError(errorContent);
        }
    }

    private void validateMessageValueLength(String value, ValidateResult result) {
        int messageValueLength = value.length();
        if (messageValueLength > 200) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(LENGTH_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "message oldValue", "200");
            result.addMessageError(errorContent);
        }
    }
}
