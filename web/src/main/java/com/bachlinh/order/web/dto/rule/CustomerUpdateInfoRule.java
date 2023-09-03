package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerUpdateInfoForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DtoValidationRule
@ActiveReflection
public class CustomerUpdateInfoRule extends AbstractRule<CustomerUpdateInfoForm> {
    private static final String INVALID_MESSAGE_ID = "MSG-000009";

    private MessageSettingRepository messageSettingRepository;
    private CustomerRepository customerRepository;

    @ActiveReflection
    public CustomerUpdateInfoRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CustomerUpdateInfoForm dto) {
        Map<String, List<String>> validationResult = new HashMap<>();

        MessageSetting rangeMessage = messageSettingRepository.getMessageById("MSG-000010");

        validateId(validationResult, dto.getId());

        Customer targetCustomer = customerRepository.getCustomerUpdatableInfo(dto.getId());

        if (!targetCustomer.getFirstName().equals(dto.getFirstName())) {
            validateFirstName(validationResult, dto.getFirstName(), rangeMessage.getValue());
        }

        if (!targetCustomer.getLastName().equals(dto.getLastName())) {
            validateLastName(validationResult, dto.getLastName(), rangeMessage.getValue());
        }

        if (!targetCustomer.getPhoneNumber().equals(dto.getPhoneNumber())) {
            validatePhone(validationResult, dto.getPhoneNumber());
        }

        if (!targetCustomer.getEmail().equals(dto.getEmail())) {
            validateEmail(validationResult, dto.getEmail());
        }

        if (!targetCustomer.getGender().equals(dto.getGender())) {
            validateGender(validationResult, dto.getGender());
        }

        if (!targetCustomer.getRole().equals(dto.getRole())) {
            validateRole(validationResult, dto.getRole());
        }

        if (!targetCustomer.getOrderPoint().equals(dto.getOrderPoint())) {
            validateOrderPoint(validationResult, dto.getOrderPoint());
        }

        return new ValidatedDto.ValidateResult() {
            @Override
            public Map<String, Object> getErrorResult() {
                return new HashMap<>(validationResult);
            }

            @Override
            public boolean shouldHandle() {
                return validationResult.isEmpty();
            }
        };
    }

    @Override
    protected void injectDependencies() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
        if (customerRepository == null) {
            customerRepository = getResolver().resolveDependencies(CustomerRepository.class);
        }
    }

    @Override
    public Class<CustomerUpdateInfoForm> applyOnType() {
        return CustomerUpdateInfoForm.class;
    }

    private void validateId(Map<String, List<String>> validationResult, String id) {
        String key = "id";
        if (!StringUtils.hasText(id)) {
            computedError("MSG-000003", key, validationResult, "Id of customer");
        } else {
            boolean isCustomerExisted = customerRepository.isCustomerIdExisted(id);
            if (!isCustomerExisted) {
                computedError("MSG-000011", key, validationResult, "Customer", id, "update");
            }
        }
    }

    private void validateFirstName(Map<String, List<String>> validationResult, String firstName, String rangeMessagePattern) {
        String key = "first_name";
        String targetName = "First name";
        if (firstName != null) {
            validateLength(validationResult, firstName, rangeMessagePattern, key, targetName);
        }
    }

    private void validateLastName(Map<String, List<String>> validationResult, String lastName, String rangeMessagePattern) {
        String key = "last_name";
        String targetName = "Last name";
        if (lastName != null) {
            validateLength(validationResult, lastName, rangeMessagePattern, key, targetName);
        }
    }

    private void validatePhone(Map<String, List<String>> validationResult, String phone) {
        String key = "phone";
        String targetName = "Phone";
        if (phone != null) {
            boolean isPhoneValid = ValidateUtils.isPhoneValid(phone);
            if (!isPhoneValid) {
                computedError(INVALID_MESSAGE_ID, key, validationResult, targetName);
            } else {
                boolean isPhoneExisted = customerRepository.isPhoneNumberExisted(phone);
                if (isPhoneExisted) {
                    computedError("MSG-000007", key, validationResult, "Phone");
                }
            }
        }
    }

    private void validateEmail(Map<String, List<String>> validationResult, String email) {
        String key = "email";
        String targetName = "Email";
        if (email != null) {
            boolean isEmailValid = ValidateUtils.isEmailValidUsingRfc5322(email);
            if (!isEmailValid) {
                computedError(INVALID_MESSAGE_ID, key, validationResult, targetName);
            } else if (customerRepository.isEmailExisted(email)) {
                computedError("MSG-000007", key, validationResult, targetName);
            }
        }
    }

    private void validateGender(Map<String, List<String>> validationResult, String gender) {
        String key = "gender";
        String targetName = "Gender";
        if (gender != null) {
            Gender genderNum = Gender.of(gender);
            if (genderNum == null) {
                computedError(INVALID_MESSAGE_ID, key, validationResult, targetName);
            }
        }
    }

    private void validateRole(Map<String, List<String>> validationResult, String role) {
        String key = "role";
        String targetName = "Role";
        if (role != null) {
            Role roleNum = Role.of(role);
            if (roleNum == null) {
                computedError(INVALID_MESSAGE_ID, key, validationResult, targetName);
            }
        }
    }

    private void validateOrderPoint(Map<String, List<String>> validationResult, Integer orderPoint) {
        String key = "order_point";
        String targetName = "Order point";
        if (orderPoint != null && orderPoint < 0) {
            computedError("MSG-000012", key, validationResult, targetName);
        }
    }

    private void validateLength(Map<String, List<String>> validationResult, String target, String lengthMessagePattern, String key, String targetName) {
        int targetLength = target.length();
        if (targetLength < 4 || targetLength > 32) {
            String message = MessageFormat.format(lengthMessagePattern, targetName, "4", "32");
            RuntimeUtils.computeMultiValueMap(key, message, validationResult);
        }
    }

    private void computedError(String messageSettingId, String key, Map<String, List<String>> validationResult, Object... params) {
        MessageSetting messageSetting = messageSettingRepository.getMessageById(messageSettingId);
        String content = MessageFormat.format(messageSetting.getValue(), params);
        RuntimeUtils.computeMultiValueMap(key, content, validationResult);
    }
}
