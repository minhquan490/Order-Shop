package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateUpdateForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateUpdateRule extends AbstractRule<EmailTemplateUpdateForm> {
    private static final String EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";
    private static final String NOT_EXISTED_MESSAGE_ID = "MSG-000017";

    private EmailTemplateFolderRepository emailTemplateFolderRepository;
    private EmailTemplateRepository emailTemplateRepository;
    private MessageSettingRepository messageSettingRepository;

    private EmailTemplateUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailTemplateUpdateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailTemplateUpdateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            computedError(key, CAN_NOT_IDENTITY_MESSAGE_ID, validateResult, "email template", "", "update");
            return createResult(validateResult);
        } else {
            if (emailTemplateRepository.isEmailTemplateExisted(dto.getId(), customer)) {
                var key = "id";
                computedError(key, NOT_EXISTED_MESSAGE_ID, validateResult, "Id of email template");
                return createResult(validateResult);
            }
        }

        EmailTemplate emailTemplate = emailTemplateRepository.getEmailTemplateForUpdate(dto.getId(), customer);

        if (!emailTemplate.getName().equals(dto.getName())) {
            validateName(dto.getName(), validateResult);
        }

        if (!emailTemplate.getTitle().equals(dto.getTitle())) {
            validateTitle(dto.getTitle(), validateResult);
        }

        if (!emailTemplate.getContent().equals(dto.getContent())) {
            validateContent(dto.getContent(), validateResult);
        }

        if (!Objects.requireNonNull(emailTemplate.getFolder().getId()).equals(dto.getFolderId())) {
            validateFolderId(dto.getFolderId(), validateResult);
        }

        if (!Arrays.equals(emailTemplate.getParams().split(","), dto.getParams())) {
            validateParams(dto.getParams(), validateResult);
        }

        return createResult(validateResult);
    }

    @Override
    protected void injectDependencies() {
        if (emailTemplateFolderRepository == null) {
            emailTemplateFolderRepository = resolveRepository(EmailTemplateFolderRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
        if (emailTemplateRepository == null) {
            emailTemplateRepository = getResolver().resolveDependencies(EmailTemplateRepository.class);
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

    private void computedError(String key, String messageId, Map<String, List<String>> validateResult, Object... contents) {
        MessageSetting messageSetting = messageSettingRepository.getMessageById(messageId);
        String errorContent = MessageFormat.format(messageSetting.getValue(), contents);
        RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
    }

    private void validateName(String name, Map<String, List<String>> validateResult) {
        if (!StringUtils.hasText(name)) {
            var key = "name";
            computedError(key, EMPTY_MESSAGE_ID, validateResult, "Name of email template");
        }
    }

    private void validateTitle(String title, Map<String, List<String>> validateResult) {
        if (!StringUtils.hasText(title)) {
            var key = "title";
            computedError(key, EMPTY_MESSAGE_ID, validateResult, "Title of email template");
        }
    }

    private void validateContent(String content, Map<String, List<String>> validateResult) {
        if (!StringUtils.hasText(content)) {
            var key = "content";
            computedError(key, EMPTY_MESSAGE_ID, validateResult, "Content of email template");
        }
    }

    private void validateFolderId(String folderId, Map<String, List<String>> validateResult) {
        var key = "folder_id";
        if (!StringUtils.hasText(folderId)) {
            computedError(key, EMPTY_MESSAGE_ID, validateResult, "Email template folder");
        } else {
            var isExist = emailTemplateFolderRepository.isEmailTemplateFolderIdExisted(folderId);
            if (isExist) {
                computedError(key, NOT_EXISTED_MESSAGE_ID, validateResult, "Folder");
            }
        }
    }

    private void validateParams(String[] params, Map<String, List<String>> validateResult) {
        if (params == null || params.length == 0) {
            var key = "params";
            computedError(key, "MSG-000015", validateResult, "Email template");
        }
    }
}
