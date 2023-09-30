package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.AddressRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = Address.class)
public class AddressValidator extends AbstractValidator<Address> {
    private static final String NOT_EMPTY_MESSAGE_ID = "MSG-000001";

    private MessageSettingRepository messageSettingRepository;
    private AddressRepository addressRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
        if (addressRepository == null) {
            addressRepository = resolveRepository(AddressRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Address entity) {
        Result result = new Result();
        MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_EMPTY_MESSAGE_ID);
        if (entity.isNew()) {
            validateValue(result, messageSetting.getValue(), entity.getValue());
            validateCity(entity.getCity(), result, messageSetting.getValue());
            validateCountry(entity.getCountry(), result, messageSetting.getValue());
            validateOwner(entity.getCustomer(), result);
        } else {
            Address old = addressRepository.getAddressForUpdate(entity.getId());
            if (!old.getCountry().equals(entity.getCountry())) {
                validateCountry(entity.getCountry(), result, messageSetting.getValue());
            }
            if (!old.getValue().equals(entity.getValue())) {
                validateValue(result, messageSetting.getValue(), entity.getValue());
            }
            if (!old.getCountry().equals(entity.getCountry())) {
                validateCountry(entity.getCountry(), result, messageSetting.getValue());
            }
        }

        return result;
    }

    private void validateValue(ValidateResult result, String errorMsgPattern, String value) {
        if (!StringUtils.hasText(value)) {
            result.addMessageError(MessageFormat.format(errorMsgPattern, "Address details"));
        }
    }

    private void validateCity(String city, ValidateResult result, String errorMsgPattern) {
        if (!StringUtils.hasText(city)) {
            result.addMessageError(MessageFormat.format(errorMsgPattern, "City"));
        }
    }

    private void validateCountry(String country, ValidateResult result, String errorMsgPattern) {
        if (!StringUtils.hasText(country)) {
            result.addMessageError(MessageFormat.format(errorMsgPattern, "Country"));
        }
    }

    private void validateOwner(Customer owner, ValidateResult result) {
        String errorMsgId = "MSG-000032";
        if (owner == null) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(errorMsgId);
            String errorMsg = MessageFormat.format(messageSetting.getValue(), "Customer", "this address");
            result.addMessageError(errorMsg);
        }
    }
}
