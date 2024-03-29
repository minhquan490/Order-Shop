package com.bachlinh.order.web.common.validator;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;

import java.text.MessageFormat;

import org.springframework.util.StringUtils;

@ActiveReflection
@ApplyOn(entity = CustomerAccessHistory.class)
public class CustomerAccessHistoryValidator extends AbstractValidator<CustomerAccessHistory> {
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
    protected ValidateResult doValidate(CustomerAccessHistory entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);
        MessageSetting nonNullMessage = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getPathRequest())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Path request"));
        }

        if (entity.getPathRequest().length() > 50) {
            result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "path request"));
        }

        if (!StringUtils.hasText(entity.getRequestType())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Request type"));
        }

        if (entity.getRequestTime() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Request time"));
        }

        if (entity.getRemoveTime() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Remove time"));
        }

        if (entity.getCustomer() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Customer associate of history"));
        }

        return result;
    }
}
