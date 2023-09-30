package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateDeleteForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateDeleteRule extends AbstractRule<EmailTemplateDeleteForm> {
    private static final String NOT_FOUND_MESSAGE_ID = "MSG-000008";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";

    private EmailTemplateRepository emailTemplateRepository;
    private MessageSettingRepository messageSettingRepository;

    private EmailTemplateDeleteRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailTemplateDeleteForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailTemplateDeleteRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateDeleteForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.id())) {
            var key = "template_id";
            MessageSetting canNotIdentityMessage = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(canNotIdentityMessage.getValue(), "template", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (emailTemplateRepository.isEmailTemplateExisted(dto.id(), customer)) {
                var key = "template_id";
                MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_FOUND_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Template");
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
        if (emailTemplateRepository == null) {
            emailTemplateRepository = resolveRepository(EmailTemplateRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailTemplateDeleteForm> applyOnType() {
        return EmailTemplateDeleteForm.class;
    }
}
