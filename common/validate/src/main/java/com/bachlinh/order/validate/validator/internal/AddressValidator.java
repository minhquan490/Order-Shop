package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
public class AddressValidator extends AbstractValidator<Address> {
    private static final String NOT_EMPTY_MESSAGE_ID = "MSG-000001";

    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public AddressValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Address entity) {
        Result result = new Result();
        MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_EMPTY_MESSAGE_ID);
        if (!StringUtils.hasText(entity.getValue())) {
            result.addMessageError(MessageFormat.format(messageSetting.getValue(), "Address details"));
        }
        if (!StringUtils.hasText(entity.getCity())) {
            result.addMessageError(MessageFormat.format(messageSetting.getValue(), "City"));
        }
        if (!StringUtils.hasText(entity.getCountry())) {
            result.addMessageError(MessageFormat.format(messageSetting.getValue(), "Country"));
        }
        return result;
    }
}
