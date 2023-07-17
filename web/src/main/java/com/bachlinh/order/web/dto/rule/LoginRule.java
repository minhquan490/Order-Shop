package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.LoginForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class LoginRule extends AbstractRule<LoginForm> {
    private static final String NOT_EMPTY_MESSAGE_ID = "MSG-000001";

    private CustomerRepository customerRepository;
    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public LoginRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(LoginForm dto) {
        var validateResult = new HashMap<String, List<String>>(2);

        MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_EMPTY_MESSAGE_ID);

        if (!StringUtils.hasText(dto.username())) {
            var key = "username";
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Username");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
        if (!StringUtils.hasText(dto.password())) {
            var key = "password";
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Password");
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
    public Class<LoginForm> applyOnType() {
        return LoginForm.class;
    }
}
