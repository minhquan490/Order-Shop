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

    @ActiveReflection
    public MessageSettingUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(MessageSettingUpdateForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getId())) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "message setting", "", "update");
            RuntimeUtils.computeMultiValueMap("id", errorContent, validationResult);
            return createResult(validationResult);
        } else {
            if (!StringUtils.hasText(dto.getValue())) {
                MessageSetting messageSetting = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Value of message setting");
                RuntimeUtils.computeMultiValueMap("value", errorContent, validationResult);
            } else {
                var messageExisted = messageSettingRepository.messageValueExisted(dto.getValue());
                if (messageExisted) {
                    MessageSetting messageSetting = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                    String errorContent = MessageFormat.format(messageSetting.getValue(), "Value of message setting");
                    RuntimeUtils.computeMultiValueMap("value", errorContent, validationResult);
                }
            }
        }
        return createResult(validationResult);
    }

    @Override
    protected void injectDependencies() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
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
}
