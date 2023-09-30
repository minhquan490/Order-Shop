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
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingUpdateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class MessageSettingUpdateRule extends AbstractRule<MessageSettingUpdateForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";

    private MessageSettingRepository messageSettingRepository;

    private MessageSettingUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<MessageSettingUpdateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new MessageSettingUpdateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(MessageSettingUpdateForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        validateId(dto.getId(), validationResult);

        if (!validationResult.isEmpty()) {
            return createResult(validationResult);
        }

        MessageSetting messageSetting = messageSettingRepository.getMessageById(dto.getId());

        if (!messageSetting.getValue().equals(dto.getValue())) {
            validateValue(dto.getValue(), validationResult);
        }

        return createResult(validationResult);
    }

    @Override
    protected void injectDependencies() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<MessageSettingUpdateForm> applyOnType() {
        return MessageSettingUpdateForm.class;
    }

    private ValidatedDto.ValidateResult createResult(Map<String, List<String>> validationResult) {
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

    private void validateId(String messageSettingId, Map<String, List<String>> validationResult) {
        String key = "id";
        if (!StringUtils.hasText(messageSettingId)) {
            computedError(key, CAN_NOT_IDENTITY_MESSAGE_ID, validationResult, "message setting", "", "update");
        } else {
            boolean isExisted = messageSettingRepository.isMessageSettingExisted(messageSettingId);
            if (!isExisted) {
                computedError(key, "MSG-000017", validationResult, "Message id");
            }
        }
    }

    private void validateValue(String value, Map<String, List<String>> validationResult) {
        String key = "value";
        if (!StringUtils.hasText(value)) {
            computedError(key, NON_EMPTY_MESSAGE_ID, validationResult, "Value of message setting");
        } else {
            var messageExisted = messageSettingRepository.messageValueExisted(value);
            if (messageExisted) {
                computedError(key, EXISTED_MESSAGE_ID, validationResult, "Value of message setting");
            }
        }
    }

    private void computedError(String key, String messageId, Map<String, List<String>> validationResult, Object... params) {
        MessageSetting messageSetting = messageSettingRepository.getMessageById(messageId);
        String errorContent = MessageFormat.format(messageSetting.getValue(), params);
        RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
    }
}
