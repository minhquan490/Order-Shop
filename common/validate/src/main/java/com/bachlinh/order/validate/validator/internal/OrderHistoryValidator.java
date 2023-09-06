package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = OrderHistory.class)
public class OrderHistoryValidator extends AbstractValidator<OrderHistory> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(OrderHistory entity) {
        Result result = new Result();

        MessageSetting nonNullMessage = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);
        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);

        if (entity.getOrderTime() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Order time"));
        }

        if (!StringUtils.hasText(entity.getOrderStatus())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Order status"));
        } else {
            if (entity.getOrderStatus().length() > 30) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "order status", "30"));
            }
        }

        if (entity.getOrder() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Order associate with this history"));
        }

        return result;
    }
}
