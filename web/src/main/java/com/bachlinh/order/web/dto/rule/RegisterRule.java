package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.customer.RegisterForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class RegisterRule extends AbstractRule<RegisterForm> {
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_KEY = "phone";
    private static final String USERNAME_KEY = "username";
    private CustomerRepository customerRepository;

    @ActiveReflection
    public RegisterRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(RegisterForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        validateCommonCase(dto, validateResult);
        validateLength(dto, validateResult);
        validatePattern(dto, validateResult);
        validateExistCase(dto, validateResult);
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
    }

    @Override
    public Class<RegisterForm> applyOnType() {
        return RegisterForm.class;
    }

    private void validateCommonCase(RegisterForm dto, Map<String, List<String>> result) {
        if (!StringUtils.hasText(dto.username())) {
            RuntimeUtils.computeMultiValueMap(USERNAME_KEY, "Username is empty", result);
        }

        if (!StringUtils.hasText(dto.firstName())) {
            var key = "first_name";
            RuntimeUtils.computeMultiValueMap(key, "First name is empty", result);
        }

        if (!StringUtils.hasText(dto.lastName())) {
            var key = "ast_name";
            RuntimeUtils.computeMultiValueMap(key, "Last name is empty", result);
        }

        if (!StringUtils.hasText(dto.phoneNumber())) {
            RuntimeUtils.computeMultiValueMap(PHONE_KEY, "Phone is empty", result);
        }

        if (!StringUtils.hasText(dto.email())) {
            RuntimeUtils.computeMultiValueMap(EMAIL_KEY, "Email is empty", result);
        }

        if (!StringUtils.hasText(dto.gender())) {
            var key = "gender";
            RuntimeUtils.computeMultiValueMap(key, "Gender is empty", result);
        }

        if (!StringUtils.hasText(dto.password())) {
            var key = "password";
            RuntimeUtils.computeMultiValueMap(key, "Password is empty", result);
        }
    }

    private void validateLength(RegisterForm dto, Map<String, List<String>> result) {
        int firstNameLength = dto.firstName().length();
        if (firstNameLength < 4 || firstNameLength > 32) {
            var key = "first_name";
            RuntimeUtils.computeMultiValueMap(key, "First name of customer must be in range 4 - 32", result);
        }

        int lastNameLength = dto.lastName().length();
        if (lastNameLength < 4 || lastNameLength > 32) {
            var key = "last_name";
            RuntimeUtils.computeMultiValueMap(key, "Last name of customer must be in range 4 - 32", result);
        }

        int usernameLength = dto.username().length();
        if (usernameLength < 4 || usernameLength > 32) {
            RuntimeUtils.computeMultiValueMap(USERNAME_KEY, "Username of customer must be in range 4 - 32", result);
        }
    }

    private void validatePattern(RegisterForm dto, Map<String, List<String>> result) {
        if (!ValidateUtils.isEmailValidUsingRfc5322(dto.email())) {
            RuntimeUtils.computeMultiValueMap(EMAIL_KEY, "Email is invalid", result);
        }

        if (!ValidateUtils.isPhoneValid(dto.phoneNumber())) {
            RuntimeUtils.computeMultiValueMap(PHONE_KEY, "Phone is invalid", result);
        }
    }

    private void validateExistCase(RegisterForm dto, Map<String, List<String>> result) {
        if (customerRepository.emailExist(dto.email())) {
            RuntimeUtils.computeMultiValueMap(EMAIL_KEY, "Email is existed", result);
        }

        if (customerRepository.phoneNumberExist(dto.phoneNumber())) {
            RuntimeUtils.computeMultiValueMap(PHONE_KEY, "Phone is existed", result);
        }

        if (customerRepository.usernameExist(dto.username())) {
            RuntimeUtils.computeMultiValueMap(USERNAME_KEY, "Username is existed", result);
        }
    }
}
