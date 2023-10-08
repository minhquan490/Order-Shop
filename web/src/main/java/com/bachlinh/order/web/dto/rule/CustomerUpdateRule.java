package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.Gender;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.core.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.customer.CustomerUpdateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CustomerUpdateRule extends AbstractRule<CustomerUpdateForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String INVALID_MESSAGE_ID = "MSG-000009";
    private static final String RANGE_INVALID_MESSAGE_ID = "MSG-000010";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";

    private CustomerRepository customerRepository;
    private MessageSettingRepository messageSettingRepository;

    private CustomerUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<CustomerUpdateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new CustomerUpdateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CustomerUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting invalidMessage = messageSettingRepository.getMessageById(INVALID_MESSAGE_ID);
        MessageSetting rangeInvalidMessage = messageSettingRepository.getMessageById(RANGE_INVALID_MESSAGE_ID);

        validateCustomerId(dto.getId(), validateResult);

        Customer updateTarget = customerRepository.getCustomerUpdatableInfo(dto.getId());

        if (!updateTarget.getFirstName().equals(dto.getFirstName())) {
            validateFirstName(dto.getFirstName(), validateResult, nonEmptyMessage, rangeInvalidMessage);
        }

        if (!updateTarget.getLastName().equals(dto.getLastName())) {
            validateLastName(dto.getLastName(), validateResult, nonEmptyMessage, rangeInvalidMessage);
        }

        if (!updateTarget.getPhoneNumber().equals(dto.getPhone())) {
            validatePhone(dto.getPhone(), validateResult, nonEmptyMessage, invalidMessage);
        }

        if (!updateTarget.getEmail().equals(dto.getEmail())) {
            validateEmail(dto.getEmail(), validateResult, nonEmptyMessage, invalidMessage);
        }

        if (!updateTarget.getGender().equalsIgnoreCase(dto.getGender())) {
            validateGender(dto.getGender(), validateResult, nonEmptyMessage);
        }

        if (!updateTarget.getUsername().equalsIgnoreCase(dto.getUsername())) {
            validateUsername(dto.getUsername(), validateResult, nonEmptyMessage, rangeInvalidMessage);
        }


        return new ValidatedDto.ValidateResult() {
            @Override
            public Map<String, Object> getErrorResult() {
                return new HashMap<>(validateResult);
            }

            @Override
            public boolean shouldHandle() {
                return validateResult.isEmpty();
            }
        };
    }

    @Override
    protected void injectDependencies() {
        if (customerRepository == null) {
            customerRepository = resolveRepository(CustomerRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<CustomerUpdateForm> applyOnType() {
        return CustomerUpdateForm.class;
    }

    private void validateCustomerId(String customerId, Map<String, List<String>> validateResult) {
        var key = "id";
        if (!StringUtils.hasText(customerId)) {
            MessageSetting canNotIdentityMessage = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(canNotIdentityMessage.getValue(), "customer", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            boolean isExisted = customerRepository.isCustomerIdExisted(customerId);
            if (!isExisted) {
                MessageSetting messageSetting = messageSettingRepository.getMessageById("MSG-000017");
                String err = MessageFormat.format(messageSetting.getValue(), customerId);
                RuntimeUtils.computeMultiValueMap(key, err, validateResult);
            }
        }
    }

    private void validateFirstName(String firstName, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting rangeInvalidMessage) {
        var key = "first_name";
        if (!StringUtils.hasText(firstName)) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "First name of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            int length = firstName.length();
            if (length < 6 || length > 32) {
                String errorContent = MessageFormat.format(rangeInvalidMessage.getValue(), "First name of customer", "6", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateLastName(String lastName, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting rangeInvalidMessage) {
        var key = "last_name";
        if (!StringUtils.hasText(lastName)) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Last name of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            int length = lastName.length();
            if (length < 6 || length > 32) {
                String errorContent = MessageFormat.format(rangeInvalidMessage.getValue(), "Last name of customer", "6", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validatePhone(String phone, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting invalidMessage) {
        var key = "phone";
        if (!StringUtils.hasText(phone)) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Phone of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isPhoneValid(phone)) {
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Phone of customer");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateEmail(String email, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting invalidMessage) {
        var key = "email";
        if (!StringUtils.hasText(email)) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Email of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isEmailValidUsingRfc5322(email)) {
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Email of customer");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateGender(String gender, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage) {
        var key = "gender";
        if (!StringUtils.hasText(gender)) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Gender of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            Gender testTarget = Gender.of(gender);
            if (testTarget == null) {
                MessageSetting messageSetting = messageSettingRepository.getMessageById("MSG-000021");
                RuntimeUtils.computeMultiValueMap(key, messageSetting.getValue(), validateResult);
            }
        }
    }

    private void validateUsername(String username, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting rangeInvalidMessage) {
        var key = "username";
        if (!StringUtils.hasText(username)) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Username of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            int length = username.length();
            if (length < 4 || length > 32) {
                String errorContent = MessageFormat.format(rangeInvalidMessage.getValue(), "Username of customer", "4", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
            MessageSetting messageSetting = messageSettingRepository.getMessageById("MSG-000035");
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Username");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
    }
}
