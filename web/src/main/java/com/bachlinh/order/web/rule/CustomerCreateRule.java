package com.bachlinh.order.web.rule;

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
import com.bachlinh.order.web.dto.form.admin.CustomerCreateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CustomerCreateRule extends AbstractRule<CustomerCreateForm> {
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_KEY = "phone";
    private static final String USERNAME_KEY = "username";

    private CustomerRepository customerRepository;

    @ActiveReflection
    public CustomerCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CustomerCreateForm dto) {
        var r = new HashMap<String, List<String>>();
        validateCommonCase(dto, r);
        validateLength(dto, r);
        validatePattern(dto, r);
        validateExistCase(dto, r);

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
    }

    @Override
    public Class<CustomerCreateForm> applyOnType() {
        return CustomerCreateForm.class;
    }

    private void validateExistCase(CustomerCreateForm dto, Map<String, List<String>> result) {
        if (customerRepository.emailExist(dto.getEmail())) {
            RuntimeUtils.computeMultiValueMap(EMAIL_KEY, "Email is existed", result);
        }

        if (customerRepository.phoneNumberExist(dto.getPhone())) {
            RuntimeUtils.computeMultiValueMap(PHONE_KEY, "Phone is existed", result);
        }

        if (customerRepository.usernameExist(dto.getUsername())) {
            RuntimeUtils.computeMultiValueMap(USERNAME_KEY, "Username is existed", result);
        }
    }

    private void validatePattern(CustomerCreateForm dto, Map<String, List<String>> result) {
        if (!ValidateUtils.isEmailValidUsingRfc5322(dto.getEmail())) {
            RuntimeUtils.computeMultiValueMap(EMAIL_KEY, "Email is invalid", result);
        }

        if (!ValidateUtils.isPhoneValid(dto.getPhone())) {
            RuntimeUtils.computeMultiValueMap(PHONE_KEY, "Phone is invalid", result);
        }
    }

    private void validateCommonCase(CustomerCreateForm dto, Map<String, List<String>> result) {

        if (!StringUtils.hasText(dto.getFirstName())) {
            var key = "first_name";
            RuntimeUtils.computeMultiValueMap(key, "First name must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getLastName())) {
            var key = "last_name";
            RuntimeUtils.computeMultiValueMap(key, "Last name must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getPhone())) {
            RuntimeUtils.computeMultiValueMap(PHONE_KEY, "Phone must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getEmail())) {
            RuntimeUtils.computeMultiValueMap(EMAIL_KEY, "Email must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getGender())) {
            var key = "gender";
            RuntimeUtils.computeMultiValueMap(key, "Gender must be choice", result);
        }

        if (!StringUtils.hasText(dto.getRole())) {
            var key = "role";
            RuntimeUtils.computeMultiValueMap(key, "Role must be choice", result);
        }

        if (!StringUtils.hasText(dto.getUsername())) {
            RuntimeUtils.computeMultiValueMap(USERNAME_KEY, "Username must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getPassword())) {
            var key = "password";
            RuntimeUtils.computeMultiValueMap(key, "Password must not be empty", result);
        }

        var address = dto.getAddress();

        if (!StringUtils.hasText(address.getHouseAddress())) {
            var key = "address.house_address";
            RuntimeUtils.computeMultiValueMap(key, "House address of customer must not be empty", result);
        }

        if (!StringUtils.hasText(address.getWard())) {
            var key = "address.ward";
            RuntimeUtils.computeMultiValueMap(key, "Ward address of customer must not be empty", result);
        }

        if (!StringUtils.hasText(address.getDistrict())) {
            var key = "address.district";
            RuntimeUtils.computeMultiValueMap(key, "District address of customer must not be empty", result);
        }

        if (!StringUtils.hasText(address.getProvince())) {
            var key = "address.province";
            RuntimeUtils.computeMultiValueMap(key, "Province address of customer must not be empty", result);
        }
    }

    private void validateLength(CustomerCreateForm dto, Map<String, List<String>> result) {
        int firstNameLength = dto.getFirstName().length();
        if (firstNameLength < 4 || firstNameLength > 32) {
            var key = "first_name";
            RuntimeUtils.computeMultiValueMap(key, "First name of customer must be in range 4 - 32", result);
        }

        int lastNameLength = dto.getLastName().length();
        if (lastNameLength < 4 || lastNameLength > 32) {
            var key = "last_name";
            RuntimeUtils.computeMultiValueMap(key, "Last name of customer must be in range 4 - 32", result);
        }

        int usernameLength = dto.getUsername().length();
        if (usernameLength < 4 || usernameLength > 32) {
            RuntimeUtils.computeMultiValueMap(USERNAME_KEY, "Username of customer must be in range 4 - 32", result);
        }
    }
}
