package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherSearchForm;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class VoucherSearchRule extends AbstractRule<VoucherSearchForm> {
    private static final String EMPTY_QUERY_MESSAGE_ID = "MSG-000026";

    private MessageSettingRepository messageSettingRepository;

    private VoucherSearchRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<VoucherSearchForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new VoucherSearchRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(VoucherSearchForm dto) {
        var validationResult = new HashMap<String, List<String>>();

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
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<VoucherSearchForm> applyOnType() {
        return VoucherSearchForm.class;
    }
}
