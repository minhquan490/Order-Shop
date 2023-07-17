package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

import java.text.MessageFormat;

@ActiveReflection
public class MessageSettingValidator extends AbstractValidator<MessageSetting> {
    private static final String LENGTH_MESSAGE_ID = "MSG-000002";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";

    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public MessageSettingValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(MessageSetting entity) {
        Result result = new Result();

        if (messageSettingRepository.messageValueExisted(entity.getValue())) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Message value");
            result.addMessageError(errorContent);
        }

        int messageValueLength = entity.getValue().length();
        if (messageValueLength > 200) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(LENGTH_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "message value", "200");
            result.addMessageError(errorContent);
        }

        return result;
    }
}
