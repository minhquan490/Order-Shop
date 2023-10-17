package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.EmailTemplateRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderUpdateForm;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private EmailTemplateRepository emailTemplateRepository;

    private EmailTemplateFolderUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailTemplateFolderUpdateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailTemplateFolderUpdateRule(environment, resolver);
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

        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var emailTemplate = emailTemplateRepository.getEmailTemplateById(dto.id(), customer);

        if (emailTemplate.getName().equals(dto.name()) && !StringUtils.hasText(dto.name())) {
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
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
        if (emailTemplateRepository == null) {
            emailTemplateRepository = resolveRepository(EmailTemplateRepository.class);
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
