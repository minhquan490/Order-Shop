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
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderCreateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateFolderCreateRule extends AbstractRule<EmailTemplateFolderCreateForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";

    private EmailTemplateFolderRepository emailTemplateFolderRepository;
    private MessageSettingRepository messageSettingRepository;

    private EmailTemplateFolderCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailTemplateFolderCreateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailTemplateFolderCreateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateFolderCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.name())) {
            var key = "name";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Name of template folder");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        var isExisted = emailTemplateFolderRepository.isEmailTemplateFolderNameExisted(dto.name());
        if (isExisted) {
            var key = "name";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Name of template folder");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
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
        if (emailTemplateFolderRepository == null) {
            emailTemplateFolderRepository = getResolver().resolveDependencies(EmailTemplateFolderRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailTemplateFolderCreateForm> applyOnType() {
        return EmailTemplateFolderCreateForm.class;
    }
}
