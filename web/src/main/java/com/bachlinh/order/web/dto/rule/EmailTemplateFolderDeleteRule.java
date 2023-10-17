package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.EmailTemplateFolderRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderDeleteForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateFolderDeleteRule extends AbstractRule<EmailTemplateFolderDeleteForm> {
    private static final String NOT_FOUND_MESSAGE_ID = "MSG-000008";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";

    private EmailTemplateFolderRepository repository;
    private MessageSettingRepository messageSettingRepository;

    private EmailTemplateFolderDeleteRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailTemplateFolderDeleteForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailTemplateFolderDeleteRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateFolderDeleteForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.id())) {
            var key = "id";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "email template folder", "", "delete");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (repository.isEmailTemplateFolderIdExisted(dto.id())) {
                var key = "id";
                MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_FOUND_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), String.format("Email template with id [%s]", dto.id()));
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
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
        if (repository == null) {
            repository = resolveRepository(EmailTemplateFolderRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailTemplateFolderDeleteForm> applyOnType() {
        return EmailTemplateFolderDeleteForm.class;
    }
}
