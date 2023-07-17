package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.enums.Gender;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.customer.RegisterForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class RegisterRule extends AbstractRule<RegisterForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String INVALID_MESSAGE_ID = "MSG-000009";
    private static final String RANGE_MESSAGE_ID = "MSG-000010";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_KEY = "phone";
    private static final String USERNAME_KEY = "username";
    private CustomerRepository customerRepository;
    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public RegisterRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(RegisterForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting rangeMessage = messageSettingRepository.getMessageById(RANGE_MESSAGE_ID);
        MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
        MessageSetting invalidMessage = messageSettingRepository.getMessageById(INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(dto.username())) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Username");
            RuntimeUtils.computeMultiValueMap(USERNAME_KEY, errorContent, validateResult);
        } else {
            int usernameLength = dto.username().length();
            if (usernameLength < 4 || usernameLength > 32) {
                String errorContent = MessageFormat.format(rangeMessage.getValue(), "Username of customer", "4", "32");
                RuntimeUtils.computeMultiValueMap(USERNAME_KEY, errorContent, validateResult);
            }
            if (customerRepository.usernameExist(dto.username())) {
                String errorContent = MessageFormat.format(existedMessage.getValue(), "Username");
                RuntimeUtils.computeMultiValueMap(USERNAME_KEY, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.firstName())) {
            var key = "first_name";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "First name");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            int firstNameLength = dto.firstName().length();
            if (firstNameLength < 4 || firstNameLength > 32) {
                var key = "first_name";
                String errorContent = MessageFormat.format(rangeMessage.getValue(), "First name of customer", "4", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.lastName())) {
            var key = "ast_name";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Last name");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            int lastNameLength = dto.lastName().length();
            if (lastNameLength < 4 || lastNameLength > 32) {
                var key = "last_name";
                String errorContent = MessageFormat.format(rangeMessage.getValue(), "Last name of customer", "4", "32");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        String phone = "Phone";
        if (!StringUtils.hasText(dto.phoneNumber())) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), phone);
            RuntimeUtils.computeMultiValueMap(PHONE_KEY, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isPhoneValid(dto.phoneNumber())) {
                String errorContent = MessageFormat.format(invalidMessage.getValue(), phone);
                RuntimeUtils.computeMultiValueMap(PHONE_KEY, errorContent, validateResult);
            }
            if (customerRepository.phoneNumberExist(dto.phoneNumber())) {
                String errorContent = MessageFormat.format(existedMessage.getValue(), phone);
                RuntimeUtils.computeMultiValueMap(PHONE_KEY, errorContent, validateResult);
            }
        }

        String email = "Email";
        if (!StringUtils.hasText(dto.email())) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), email);
            RuntimeUtils.computeMultiValueMap(EMAIL_KEY, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isEmailValidUsingRfc5322(dto.email())) {
                String errorContent = MessageFormat.format(invalidMessage.getValue(), email);
                RuntimeUtils.computeMultiValueMap(EMAIL_KEY, errorContent, validateResult);
            }
            if (customerRepository.emailExist(dto.email())) {
                String errorContent = MessageFormat.format(existedMessage.getValue(), email);
                RuntimeUtils.computeMultiValueMap(EMAIL_KEY, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.gender())) {
            var key = "gender";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Gender");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (Gender.of(dto.gender()) == null) {
                var key = "gender";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Gender");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.password())) {
            var key = "password";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Password");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
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
    public Class<RegisterForm> applyOnType() {
        return RegisterForm.class;
    }
}
