package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
public class LoginHistoryValidator extends AbstractValidator<LoginHistory> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";

    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public LoginHistoryValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(LoginHistory entity) {
        Result result = new Result();

        MessageSetting nonNullMessage = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);
        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);

        if (entity.getLastLoginTime() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Last login time"));
        }

        if (!StringUtils.hasText(entity.getLoginIp())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Login ip"));
        } else {
            if (entity.getLoginIp().length() > 30) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "login ip", "30"));
            }
        }

        if (entity.getSuccess() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Login success"));
        }

        if (entity.getCustomer() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Customer associate"));
        }

        return result;
    }
}
