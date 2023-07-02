package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.LoginForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class LoginRule extends AbstractRule<LoginForm> {
    private CustomerRepository customerRepository;

    @ActiveReflection
    public LoginRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(LoginForm dto) {
        var validateResult = new HashMap<String, List<String>>(2);
        validateCommonCase(dto, validateResult);
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
    public Class<LoginForm> applyOnType() {
        return LoginForm.class;
    }

    private void validateCommonCase(LoginForm dto, Map<String, List<String>> result) {
        if (!StringUtils.hasText(dto.username())) {
            var key = "username";
            RuntimeUtils.computeMultiValueMap(key, "Username is empty", result);
        }
        if (!StringUtils.hasText(dto.password())) {
            var key = "password";
            RuntimeUtils.computeMultiValueMap(key, "Password is empty", result);
        }
    }
}
