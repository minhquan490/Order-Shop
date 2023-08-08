package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
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

    @ActiveReflection
    public CustomerValidator(DependenciesResolver resolver) {
        super(resolver);
    }

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

        if (!StringUtils.hasText(entity.getUsername())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Username"));
        } else {
            if (entity.getUsername().length() > 24) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "username", "24"));
            }
            if (customerRepository.usernameExist(entity.getUsername())) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), "Username"));
            }
        }

        String messageFormatArg = "Email";

        if (!StringUtils.hasText(entity.getEmail())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), messageFormatArg));
        } else {
            if (entity.getEmail().length() > 32) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "email", "32"));
            }
            if (!ValidateUtils.isEmailValidUsingRfc2822(entity.getEmail())) {
                result.addMessageError(MessageFormat.format(invalidMessage.getValue(), messageFormatArg));
            }
            if (customerRepository.emailExist(entity.getEmail())) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), "Email"));
            }
        }

        if (entity.isNew()) {
            return result;
        }

        if (entity.getGender().isEmpty()) {
            result.addMessageError(genderInvalid.getValue());
        }

        if (!StringUtils.hasText(entity.getFirstName())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "First name"));
        } else {
            if (entity.getFirstName().length() > 36) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "first name", "36"));
            }
        }

        if (!StringUtils.hasText(entity.getLastName())) {

            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Last name"));
        } else {
            if (entity.getLastName().length() > 36) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "last name", "36"));
            }
        }

        if (!StringUtils.hasText(entity.getPhoneNumber())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Phone number"));
        } else {
            if (entity.getPhoneNumber().length() != 10) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "phone number", "10"));
            }
            if (customerRepository.phoneNumberExist(entity.getPhoneNumber())) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), "Phone number"));
            }
        }
        return result;
    }
}
