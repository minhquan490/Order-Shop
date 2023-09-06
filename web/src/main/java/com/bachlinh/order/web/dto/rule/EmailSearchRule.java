package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.EmailSearchForm;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailSearchRule extends AbstractRule<EmailSearchForm> {
    private static final String EMPTY_QUERY_MESSAGE_ID = "MSG-000026";
    private MessageSettingRepository messageSettingRepository;

    private EmailSearchRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailSearchForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailSearchRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailSearchForm dto) {
        var validationResult = new HashMap<String, List<String>>(1);

        if (!StringUtils.hasText(dto.getQuery())) {
            var key = "query";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(EMPTY_QUERY_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap(key, messageSetting.getValue(), validationResult);
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
    }

    @Override
    public Class<EmailSearchForm> applyOnType() {
        return EmailSearchForm.class;
    }
}
