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

    @ActiveReflection
    public CustomerUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CustomerUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting invalidMessage = messageSettingRepository.getMessageById(INVALID_MESSAGE_ID);
        MessageSetting rangeInvalidMessage = messageSettingRepository.getMessageById(RANGE_INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            MessageSetting canNotIdentityMessage = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(canNotIdentityMessage.getValue(), "customer", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getFirstName())) {
            var key = "first_name";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "First name of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (dto.getFirstName().length() < 6 || dto.getFirstName().length() > 32) {
                var key = "first_name";
                String errorContent = MessageFormat.format(rangeInvalidMessage.getValue(), "First name of customer", "6", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.getLastName())) {
            var key = "last_name";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Last name of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (dto.getLastName().length() < 6 || dto.getLastName().length() > 32) {
                var key = "last_name";
                String errorContent = MessageFormat.format(rangeInvalidMessage.getValue(), "Last name of customer", "6", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.getPhone())) {
            var key = "phone";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Phone of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isPhoneValid(dto.getPhone())) {
                var key = "phone";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Phone of customer");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.getEmail())) {
            var key = "email";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Email of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isEmailValidUsingRfc5322(dto.getEmail())) {
                var key = "email";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Email of customer");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.getGender())) {
            var key = "gender";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Gender of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getUsername())) {
            var key = "username";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Username of customer");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (dto.getUsername().length() < 4 || dto.getUsername().length() > 32) {
                var key = "username";
                String errorContent = MessageFormat.format(rangeInvalidMessage.getValue(), "Username of customer", "4", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
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
            customerRepository = getResolver().resolveDependencies(CustomerRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<CustomerUpdateForm> applyOnType() {
        return CustomerUpdateForm.class;
    }
}
