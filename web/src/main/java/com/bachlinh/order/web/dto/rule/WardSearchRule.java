package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.WardSearchForm;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class WardSearchRule extends AbstractRule<WardSearchForm> {
    private static final String EMPTY_QUERY_MESSAGE_ID = "MSG-000026";

    private MessageSettingRepository messageSettingRepository;

    private WardSearchRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<WardSearchForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new WardSearchRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(WardSearchForm dto) {
        HashMap<String, List<String>> validationResult = HashMap.newHashMap(1);

        if (!StringUtils.hasText(dto.getQuery())) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(EMPTY_QUERY_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap("query", messageSetting.getValue(), validationResult);
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
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<WardSearchForm> applyOnType() {
        return WardSearchForm.class;
    }
}
