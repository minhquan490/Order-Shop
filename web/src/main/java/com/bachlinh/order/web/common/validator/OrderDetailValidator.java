package com.bachlinh.order.web.common.validator;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = OrderDetail.class)
public class OrderDetailValidator extends AbstractValidator<OrderDetail> {
    private static final String NEGATIVE_INVALID_MESSAGE_ID = "MSG-000013";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(OrderDetail entity) {
        Result result = new Result();

        MessageSetting negativeInvalidMessage = messageSettingRepository.getMessageById(NEGATIVE_INVALID_MESSAGE_ID);

        if (entity.getAmount() <= 0) {
            result.addMessageError(MessageFormat.format(negativeInvalidMessage.getValue(), "Order amount"));
        }
        return result;
    }
}
