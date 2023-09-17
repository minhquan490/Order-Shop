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
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateCreateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateCreateRule extends AbstractRule<EmailTemplateCreateForm> {
    private static final String EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String SPECIFY_MESSAGE_ID = "MSG-000014";

    private MessageSettingRepository messageSettingRepository;

    private EmailTemplateCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailTemplateCreateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailTemplateCreateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting emptyMessage = messageSettingRepository.getMessageById(EMPTY_MESSAGE_ID);

        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Name of template");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getTitle())) {
            var key = "title";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Title of template");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getContent())) {
            var key = "content";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Content of template");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (dto.getParams() == null || dto.getParams().length == 0) {
            var key = "params";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(SPECIFY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Params for template");
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
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailTemplateCreateForm> applyOnType() {
        return EmailTemplateCreateForm.class;
    }
}
