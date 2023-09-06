package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = Customer.class)
public class CustomerValidator extends AbstractValidator<Customer> {
    private static final String NONE_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String INVALID_MESSAGE_ID = "MSG-000009";
    private static final String GENDER_INVALID = "MSG-000021";

    private CustomerRepository customerRepository;
    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (customerRepository == null) {
            customerRepository = getResolver().resolveDependencies(CustomerRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Customer entity) {
        ValidateResult result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NONE_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);
        MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
        MessageSetting invalidMessage = messageSettingRepository.getMessageById(INVALID_MESSAGE_ID);
        MessageSetting genderInvalid = messageSettingRepository.getMessageById(GENDER_INVALID);

        if (entity.isNew()) {
            validateUsername(result, entity.getUsername(), nonEmptyMessage, lengthInvalidMessage, existedMessage);
            validateEmail(result, entity.getEmail(), nonEmptyMessage, lengthInvalidMessage, existedMessage, invalidMessage);
        } else {
            Customer targetCustomer = customerRepository.getCustomerUpdatableInfo(entity.getId());
            if (!targetCustomer.getUsername().equals(entity.getUsername())) {
                validateUsername(result, entity.getUsername(), nonEmptyMessage, lengthInvalidMessage, existedMessage);
            }
            if (!targetCustomer.getEmail().equals(entity.getEmail())) {
                validateEmail(result, entity.getEmail(), nonEmptyMessage, lengthInvalidMessage, existedMessage, invalidMessage);
            }
            if (!targetCustomer.getGender().equals(entity.getGender())) {
                validateGender(result, entity.getGender(), genderInvalid, invalidMessage);
            }
            if (!targetCustomer.getFirstName().equals(entity.getFirstName())) {
                validateFirstName(result, entity.getFirstName(), nonEmptyMessage, lengthInvalidMessage);
            }
            if (!targetCustomer.getLastName().equals(entity.getLastName())) {
                validateLastName(result, entity.getLastName(), nonEmptyMessage, lengthInvalidMessage);
            }
            if (!targetCustomer.getPhoneNumber().equals(entity.getPhoneNumber())) {
                validatePhoneNumber(result, entity.getPhoneNumber(), nonEmptyMessage, lengthInvalidMessage, existedMessage);
            }
        }
        return result;
    }

    private void validateUsername(ValidateResult result, String username, MessageSetting nonEmptyMessage, MessageSetting lengthInvalidMessage, MessageSetting existedMessage) {
        if (!StringUtils.hasText(username)) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Username"));
        } else {
            if (username.length() > 24) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "username", "24"));
            }
            if (customerRepository.isUsernameExisted(username)) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), "Username"));
            }
        }
    }

    private void validateEmail(ValidateResult result, String email, MessageSetting nonEmptyMessage, MessageSetting lengthInvalidMessage, MessageSetting existedMessage, MessageSetting invalidMessage) {
        String messageFormatArg = "Email";
        if (!StringUtils.hasText(email)) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), messageFormatArg));
        } else {
            if (email.length() > 80) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "email", "32"));
            }
            if (!ValidateUtils.isEmailValidUsingRfc2822(email)) {
                result.addMessageError(MessageFormat.format(invalidMessage.getValue(), messageFormatArg));
            }
            if (customerRepository.isEmailExisted(email)) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), messageFormatArg));
            }
        }
    }

    private void validateGender(ValidateResult result, String gender, MessageSetting genderInvalid, MessageSetting invalidMessage) {
        if (gender.isEmpty()) {
            result.addMessageError(genderInvalid.getValue());
        } else {
            Gender g = Gender.of(gender);
            if (g == null) {
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Gender");
                result.addMessageError(errorContent);
            }
        }
    }

    private void validateFirstName(ValidateResult result, String firstName, MessageSetting nonEmptyMessage, MessageSetting lengthInvalidMessage) {
        if (!StringUtils.hasText(firstName)) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "First name"));
        } else {
            if (firstName.length() > 36) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "first name", "36"));
            }
        }
    }

    private void validateLastName(ValidateResult result, String lastName, MessageSetting nonEmptyMessage, MessageSetting lengthInvalidMessage) {
        if (!StringUtils.hasText(lastName)) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Last name"));
        } else {
            if (lastName.length() > 36) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "last name", "36"));
            }
        }
    }

    private void validatePhoneNumber(ValidateResult result, String phoneNumber, MessageSetting nonEmptyMessage, MessageSetting lengthInvalidMessage, MessageSetting existedMessage) {
        if (!StringUtils.hasText(phoneNumber)) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Phone number"));
        } else {
            if (phoneNumber.length() != 10) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "phone number", "10"));
            }
            if (customerRepository.isPhoneNumberExisted(phoneNumber)) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), "Phone number"));
            }
        }
    }
}
