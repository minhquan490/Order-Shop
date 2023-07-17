package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

import java.text.MessageFormat;

@ActiveReflection
public class CartValidator extends AbstractValidator<Cart> {
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";

    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public CartValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
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
