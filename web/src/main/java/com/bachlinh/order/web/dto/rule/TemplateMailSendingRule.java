package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.sending.TemplateMailSendingForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class TemplateMailSendingRule extends AbstractRule<TemplateMailSendingForm> {
    private static final String NOT_FOUND_MESSAGE_ID = "MSG-000008";
    private static final String MUST_BE_SPECIFIC_MESSAGE_ID = "MSG-000014";
    private static final String TEMPLATE_MAIL_NOT_FOUND_MESSAGE_ID = "MSG-000030";
    private EmailTemplateRepository emailTemplateRepository;
    private CustomerRepository customerRepository;
    private MessageSettingRepository messageSettingRepository;

    private TemplateMailSendingRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<TemplateMailSendingForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new TemplateMailSendingRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(TemplateMailSendingForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!StringUtils.hasText(dto.getTemplateId())) {
            var key = "template_id";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(TEMPLATE_MAIL_NOT_FOUND_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap(key, messageSetting.getValue(), validateResult);
        }

        if (!StringUtils.hasText(dto.getToCustomer())) {
            var key = "to";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(MUST_BE_SPECIFIC_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Receiver");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        MessageSetting notFoundMessage = messageSettingRepository.getMessageById(NOT_FOUND_MESSAGE_ID);

        if (emailTemplateRepository.isEmailTemplateExisted(dto.getTemplateId(), customer)) {
            var key = "template_id";
            String errorContent = MessageFormat.format(notFoundMessage.getValue(), "Email template");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (customerRepository.isCustomerIdExisted(dto.getToCustomer())) {
            var key = "to";
            String errorContent = MessageFormat.format(notFoundMessage.getValue(), "Receiver");
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
        if (emailTemplateRepository == null) {
            emailTemplateRepository = resolveRepository(EmailTemplateRepository.class);
        }
        if (customerRepository == null) {
            customerRepository = resolveRepository(CustomerRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<TemplateMailSendingForm> applyOnType() {
        return TemplateMailSendingForm.class;
    }
}
