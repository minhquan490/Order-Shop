package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = Order.class)
public class OrderValidator extends AbstractValidator<Order> {
    private static final String SPECIFIC_INVALID_MESSAGE_ID = "MSG-000014";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Order entity) {
        Result result = new Result();

        MessageSetting specificInvalidMessage = messageSettingRepository.getMessageById(SPECIFIC_INVALID_MESSAGE_ID);

        if (entity.getTimeOrder() == null) {
            result.addMessageError(MessageFormat.format(specificInvalidMessage.getValue(), "Time order"));
        }
        return result;
    }
}
