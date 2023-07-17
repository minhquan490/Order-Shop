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
import com.bachlinh.order.web.dto.form.common.DistrictSearchForm;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class DistrictSearchRule extends AbstractRule<DistrictSearchForm> {
    private MessageSettingRepository messageSettingRepository;
    private static final String SEARCH_WITH_EMPTY_KEYWORD_MESSAGE_ID = "MSG-000026";

    @ActiveReflection
    public DistrictSearchRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(DistrictSearchForm dto) {
        var validationResult = new HashMap<String, List<String>>();
        if (!StringUtils.hasText(dto.getQuery())) {
            var key = "query";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(SEARCH_WITH_EMPTY_KEYWORD_MESSAGE_ID);
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
    public Class<DistrictSearchForm> applyOnType() {
        return DistrictSearchForm.class;
    }
}
