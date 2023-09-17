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
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingDeleteForm;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DtoValidationRule
@ActiveReflection
public class MessageSettingDeleteRule extends AbstractRule<MessageSettingDeleteForm> {
    private static final String NON_EXISTED_MESSAGE_ID = "MSG-000017";

    private MessageSettingRepository messageSettingRepository;

    private MessageSettingDeleteRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<MessageSettingDeleteForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new MessageSettingDeleteRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(MessageSettingDeleteForm dto) {
        var validationResult = new HashMap<String, List<String>>(1);

        if (messageSettingRepository.isMessageSettingExisted(dto.getId())) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(NON_EXISTED_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), String.format("Message setting with id [%s]", dto.getId()));
            RuntimeUtils.computeMultiValueMap("id", errorContent, validationResult);
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
    public Class<MessageSettingDeleteForm> applyOnType() {
        return MessageSettingDeleteForm.class;
    }
}
