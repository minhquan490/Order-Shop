package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

import java.text.MessageFormat;

@ActiveReflection
public class CartDetailValidator extends AbstractValidator<CartDetail> {
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";
    private static final String NON_CONTAIN_PRODUCT_MESSAGE_ID = "MSG-000004";
    private static final String NOT_IN_CART_MESSAGE_ID = "MSG-000005";

    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public CartDetailValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(CartDetail entity) {
        Result result = new Result();

        MessageSetting nonNullMessage = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);
        MessageSetting nonContainProductMessage = messageSettingRepository.getMessageById(NON_CONTAIN_PRODUCT_MESSAGE_ID);
        MessageSetting notInCartMessage = messageSettingRepository.getMessageById(NOT_IN_CART_MESSAGE_ID);

        if (entity.getAmount() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Number amount of product"));
        }

        if (entity.getProduct() == null) {
            result.addMessageError(nonContainProductMessage.getValue());
        }

        if (entity.getCart() == null) {
            result.addMessageError(notInCartMessage.getValue());
        }

        return result;
    }
}