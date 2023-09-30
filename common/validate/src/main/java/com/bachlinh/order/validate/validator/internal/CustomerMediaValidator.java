package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.CustomerMedia;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = CustomerMedia.class)
public class CustomerMediaValidator extends AbstractValidator<CustomerMedia> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(CustomerMedia entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting nonNullMessage = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getUrl())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Url of media"));
        }

        if (entity.getUrl().length() > 100) {
            result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "url", "100"));
        }

        if (!StringUtils.hasText(entity.getContentType())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Content type of media"));
        }

        if (entity.getContentType().length() > 100) {
            result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "content type", "100"));
        }

        if (entity.getContentLength() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Content length of media"));
        }

        if (entity.getCustomer() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Customer of media"));
        }

        return result;
    }
}
