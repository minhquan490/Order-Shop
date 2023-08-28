package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerCreateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CustomerCreateRule extends AbstractRule<CustomerCreateForm> {
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_KEY = "phone";
    private static final String USERNAME_KEY = "username";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String INVALID_MESSAGE_ID = "MSG-000009";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String RANGE_MESSAGE_ID = "MSG-000010";

    private CustomerRepository customerRepository;
    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public CustomerCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CustomerCreateForm dto) {
        var r = new HashMap<String, List<String>>();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting rangeMessage = messageSettingRepository.getMessageById(RANGE_MESSAGE_ID);
        MessageSetting invalidMessage = messageSettingRepository.getMessageById(INVALID_MESSAGE_ID);
        MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);

        String firstName = "First name";
        if (!StringUtils.hasText(dto.getFirstName())) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), firstName);
            RuntimeUtils.computeMultiValueMap(FIRST_NAME_KEY, errorContent, r);
        } else {
            int firstNameLength = dto.getFirstName().length();
            if (firstNameLength < 4 || firstNameLength > 32) {
                String content = MessageFormat.format(rangeMessage.getValue(), firstName, "4", "32");
                RuntimeUtils.computeMultiValueMap(FIRST_NAME_KEY, content, r);
            }
        }

        String lastName = "Last name";
        if (!StringUtils.hasText(dto.getLastName())) {
            var key = "last_name";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), lastName);
            RuntimeUtils.computeMultiValueMap(key, errorContent, r);
        } else {
            int lastNameLength = dto.getLastName().length();
            if (lastNameLength < 4 || lastNameLength > 32) {
                var key = "last_name";
                String errorContent = MessageFormat.format(rangeMessage.getValue(), lastName, "4", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, r);
            }
        }

        String username = "Username";
        if (!StringUtils.hasText(dto.getUsername())) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), username);
            RuntimeUtils.computeMultiValueMap(USERNAME_KEY, errorContent, r);
        } else {
            int usernameLength = dto.getUsername().length();
            if (usernameLength < 4 || usernameLength > 32) {
                String errorContent = MessageFormat.format(rangeMessage.getValue(), username, "4", "32");
                RuntimeUtils.computeMultiValueMap(USERNAME_KEY, errorContent, r);
            } else if (customerRepository.isUsernameExisted(dto.getUsername())) {
                String errorContent = MessageFormat.format(existedMessage.getValue(), "Username");
                RuntimeUtils.computeMultiValueMap(USERNAME_KEY, errorContent, r);
            }
        }

        if (!StringUtils.hasText(dto.getPhone())) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Phone name");
            RuntimeUtils.computeMultiValueMap(PHONE_KEY, errorContent, r);
        } else {
            if (!ValidateUtils.isPhoneValid(dto.getPhone())) {
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Phone");
                RuntimeUtils.computeMultiValueMap(PHONE_KEY, errorContent, r);
            } else if (customerRepository.isPhoneNumberExisted(dto.getPhone())) {
                String errorContent = MessageFormat.format(existedMessage.getValue(), "Phone");
                RuntimeUtils.computeMultiValueMap(PHONE_KEY, errorContent, r);
            }
        }

        String email = "Email";
        if (!StringUtils.hasText(dto.getEmail())) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), email);
            RuntimeUtils.computeMultiValueMap(EMAIL_KEY, errorContent, r);
        } else {
            if (!ValidateUtils.isEmailValidUsingRfc5322(dto.getEmail())) {
                String errorContent = MessageFormat.format(invalidMessage.getValue(), email);
                RuntimeUtils.computeMultiValueMap(EMAIL_KEY, errorContent, r);
            } else if (customerRepository.isEmailExisted(dto.getEmail())) {
                String errorContent = MessageFormat.format(existedMessage.getValue(), email);
                RuntimeUtils.computeMultiValueMap(EMAIL_KEY, errorContent, r);
            }
        }

        if (!StringUtils.hasText(dto.getGender())) {
            var key = "gender";
            RuntimeUtils.computeMultiValueMap(key, "Gender must be choice", r);
        }

        if (!StringUtils.hasText(dto.getRole())) {
            var key = "role";
            RuntimeUtils.computeMultiValueMap(key, "Role must be choice", r);
        }

        if (!StringUtils.hasText(dto.getPassword())) {
            var key = "password";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Password");
            RuntimeUtils.computeMultiValueMap(key, errorContent, r);
        }

        var address = dto.getAddress();

        if (!StringUtils.hasText(address.getHouseAddress())) {
            var key = "address.house_address";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "House address of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, r);
        }

        if (!StringUtils.hasText(address.getWard())) {
            var key = "address.ward";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Ward address of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, r);
        }

        if (!StringUtils.hasText(address.getDistrict())) {
            var key = "address.district";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "District address of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, r);
        }

        if (!StringUtils.hasText(address.getProvince())) {
            var key = "address.province";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Province address of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, r);
        }

        return new ValidatedDto.ValidateResult() {
            @Override
            public Map<String, Object> getErrorResult() {
                Map<String, Object> result = new HashMap<>(r.size());
                result.putAll(r);
                return result;
            }

            @Override
            public boolean shouldHandle() {
                return r.isEmpty();
            }
        };
    }

    @Override
    protected void injectDependencies() {
        if (customerRepository == null) {
            customerRepository = getResolver().resolveDependencies(CustomerRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<CustomerCreateForm> applyOnType() {
        return CustomerCreateForm.class;
    }
}
