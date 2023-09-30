package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = OrderStatus.class)
public class OrderStatusValidator extends AbstractValidator<OrderStatus> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String ASSOCIATE_INVALID_MESSAGE_ID = "MSG-000032";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(OrderStatus entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting associateInvalidMessage = messageSettingRepository.getMessageById(ASSOCIATE_INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getStatus())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Order status"));
        }
        if (entity.getOrder() == null) {
            result.addMessageError(MessageFormat.format(associateInvalidMessage.getValue(), "Order", "order status"));
        }
        return result;
    }
}
