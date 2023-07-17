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
import com.bachlinh.order.web.dto.form.common.RestoreEmailForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class RestoreEmailRule extends AbstractRule<RestoreEmailForm> {
    private static final String CAN_NOT_STORE_EMAIL_MESSAGE_ID = "MSG-000029";
    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public RestoreEmailRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(RestoreEmailForm dto) {
        var validateResult = new HashMap<String, List<String>>(1);
        if (dto.getEmailIds().length == 0) {
            var key = "email_ids";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(CAN_NOT_STORE_EMAIL_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap(key, messageSetting.getValue(), validateResult);
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
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<RestoreEmailForm> applyOnType() {
        return RestoreEmailForm.class;
    }
}
