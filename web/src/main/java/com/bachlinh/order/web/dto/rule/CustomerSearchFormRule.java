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
import com.bachlinh.order.web.dto.form.admin.customer.CustomerSearchForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CustomerSearchFormRule extends AbstractRule<CustomerSearchForm> {
    private static final String CAN_NOT_SEARCH_MESSAGE_ID = "MSG-000019";

    private MessageSettingRepository messageSettingRepository;

    public CustomerSearchFormRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CustomerSearchForm dto) {
        var validationResult = new HashMap<String, List<String>>(1);
        
        if (!StringUtils.hasText(dto.getQuery())) {
            var key = "query";
            MessageSetting canNotSearchMessage = messageSettingRepository.getMessageById(CAN_NOT_SEARCH_MESSAGE_ID);
            String errorContent = MessageFormat.format(canNotSearchMessage.getValue(), "with empty query");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
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
    public Class<CustomerSearchForm> applyOnType() {
        return CustomerSearchForm.class;
    }
}
