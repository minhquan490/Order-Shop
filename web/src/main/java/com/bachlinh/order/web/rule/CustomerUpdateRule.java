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
import com.bachlinh.order.web.dto.form.admin.CustomerUpdateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CustomerUpdateRule extends AbstractRule<CustomerUpdateForm> {
    private CustomerRepository customerRepository;

    @ActiveReflection
    public CustomerUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CustomerUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        validateCommonCase(dto, validateResult);
        validateWithRegex(dto, validateResult);
        validateLength(dto, validateResult);
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
    public Class<CustomerUpdateForm> applyOnType() {
        return CustomerUpdateForm.class;
    }

    private void validateCommonCase(CustomerUpdateForm dto, Map<String, List<String>> result) {
        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity customer for update", result);
        }
        if (!StringUtils.hasText(dto.getFirstName())) {
            var key = "first_name";
            RuntimeUtils.computeMultiValueMap(key, "First name of customer can not be empty", result);
        }
        if (!StringUtils.hasText(dto.getLastName())) {
            var key = "last_name";
            RuntimeUtils.computeMultiValueMap(key, "Last name of customer can not be empty", result);
        }
        if (!StringUtils.hasText(dto.getPhone())) {
            var key = "phone";
            RuntimeUtils.computeMultiValueMap(key, "Phone of customer can not be empty", result);
        }
        if (!StringUtils.hasText(dto.getEmail())) {
            var key = "email";
            RuntimeUtils.computeMultiValueMap(key, "Email of customer can not be empty", result);
        }
        if (!StringUtils.hasText(dto.getGender())) {
            var key = "gender";
            RuntimeUtils.computeMultiValueMap(key, "Gender of customer can not be empty", result);
        }
        if (!StringUtils.hasText(dto.getUsername())) {
            var key = "username";
            RuntimeUtils.computeMultiValueMap(key, "Username of customer can not be empty", result);
        }
    }

    private void validateWithRegex(CustomerUpdateForm dto, Map<String, List<String>> result) {
        if (!ValidateUtils.isPhoneValid(dto.getPhone())) {
            var key = "phone";
            RuntimeUtils.computeMultiValueMap(key, "Phone of customer is invalid", result);
        }
        if (!ValidateUtils.isEmailValidUsingRfc5322(dto.getEmail())) {
            var key = "email";
            RuntimeUtils.computeMultiValueMap(key, "Email of customer is invalid", result);
        }
    }

    private void validateLength(CustomerUpdateForm dto, Map<String, List<String>> result) {
        if (dto.getFirstName().length() < 6 || dto.getFirstName().length() > 32) {
            var key = "first_name";
            RuntimeUtils.computeMultiValueMap(key, "Customer first name must be in range 6 - 32", result);
        }
        if (dto.getLastName().length() < 6 || dto.getLastName().length() > 32) {
            var key = "last_name";
            RuntimeUtils.computeMultiValueMap(key, "Customer last name must be in range 6 - 32", result);
        }
        if (dto.getUsername().length() < 4 || dto.getUsername().length() > 32) {
            var key = "username";
            RuntimeUtils.computeMultiValueMap(key, "Username must be in range 4 - 32", result);
        }
    }
}
