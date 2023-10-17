package com.bachlinh.order.web.common.validator;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = Cart.class)
public class CartValidator extends AbstractValidator<Cart> {
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Cart entity) {
        Result result = new Result();

        MessageSetting messageSetting = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);

        if (entity.getCustomer() == null) {
            result.addMessageError(MessageFormat.format(messageSetting.getValue(), "Owner of cart"));
        }

        return result;
    }
}
