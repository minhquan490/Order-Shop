package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateUpdateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateUpdateRule extends AbstractRule<EmailTemplateUpdateForm> {
    private static final String EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";
    private static final String NOT_EXISTED_MESSAGE_ID = "MSG-000017";

    private EmailTemplateFolderRepository emailTemplateFolderRepository;
    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public EmailTemplateUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting emptyMessage = messageSettingRepository.getMessageById(EMPTY_MESSAGE_ID);

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "email template", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            return createResult(validateResult);
        }

        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Name of email template");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getTitle())) {
            var key = "title";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Title of email template");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getContent())) {
            var key = "content";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Content of email template");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getFolderId())) {
            var key = "folder_id";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Email template folder");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            var isExist = emailTemplateFolderRepository.isEmailTemplateFolderIdExisted(dto.getFolderId());
            if (isExist) {
                var key = "folder_id";
                MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Folder");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        if (dto.getParams() == null || dto.getParams().length == 0) {
            var key = "params";
            RuntimeUtils.computeMultiValueMap(key, "Email template must have at least 1 param", validateResult);
        }
        return createResult(validateResult);
    }

    @Override
    protected void injectDependencies() {
        if (emailTemplateFolderRepository == null) {
            emailTemplateFolderRepository = getResolver().resolveDependencies(EmailTemplateFolderRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailTemplateUpdateForm> applyOnType() {
        return EmailTemplateUpdateForm.class;
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
