package com.bachlinh.order.web.dto.rule;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.sending.TemplateMailSendingForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class TemplateMailSendingRule extends AbstractRule<TemplateMailSendingForm> {
    private EmailTemplateRepository emailTemplateRepository;
    private CustomerRepository customerRepository;

    @ActiveReflection
    public TemplateMailSendingRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(TemplateMailSendingForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!StringUtils.hasText(dto.getTemplateId())) {
            var key = "template_id";
            RuntimeUtils.computeMultiValueMap(key, "Can not find email template for sending", validateResult);
        }

        if (!StringUtils.hasText(dto.getToCustomer())) {
            var key = "to";
            RuntimeUtils.computeMultiValueMap(key, "Receiver must specify", validateResult);
        }

        if (emailTemplateRepository.isEmailTemplateExisted(dto.getTemplateId(), customer)) {
            var key = "template_id";
            RuntimeUtils.computeMultiValueMap(key, "Email template not found", validateResult);
        }

        if (customerRepository.existById(dto.getToCustomer())) {
            var key = "to";
            RuntimeUtils.computeMultiValueMap(key, "Receiver not found", validateResult);
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
            emailTemplateRepository = getResolver().resolveDependencies(EmailTemplateRepository.class);
        }
        if (customerRepository == null) {
            customerRepository = getResolver().resolveDependencies(CustomerRepository.class);
        }
    }

    @Override
    public Class<TemplateMailSendingForm> applyOnType() {
        return TemplateMailSendingForm.class;
    }
}
