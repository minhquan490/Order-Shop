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
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingCreateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class MessageSettingCreateRule extends AbstractRule<MessageSettingCreateForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";

    private MessageSettingRepository messageSettingRepository;

    private MessageSettingCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<MessageSettingCreateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new MessageSettingCreateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(MessageSettingCreateForm dto) {
        HashMap<String, List<String>> validationResult = HashMap.newHashMap(1);

        if (!StringUtils.hasText(dto.getValue())) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Value of message setting");
            RuntimeUtils.computeMultiValueMap("oldValue", errorContent, validationResult);
        } else {
            var valueExisted = messageSettingRepository.messageValueExisted(dto.getValue());
            if (valueExisted) {
                MessageSetting messageSetting = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Value of message setting");
                RuntimeUtils.computeMultiValueMap("oldValue", errorContent, validationResult);
            }
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
    public Class<MessageSettingCreateForm> applyOnType() {
        return MessageSettingCreateForm.class;
    }
}
