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
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderUpdateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateFolderUpdateRule extends AbstractRule<EmailTemplateFolderUpdateForm> {
    private static final String EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";

    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public EmailTemplateFolderUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateFolderUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.id())) {
            var key = "id";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "template folder", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            return createResult(validateResult);
        }

        if (!StringUtils.hasText(dto.name())) {
            var key = "name";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(EMPTY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Name of template folder");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
        return createResult(validateResult);
    }

    @Override
    protected void injectDependencies() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailTemplateFolderUpdateForm> applyOnType() {
        return EmailTemplateFolderUpdateForm.class;
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
