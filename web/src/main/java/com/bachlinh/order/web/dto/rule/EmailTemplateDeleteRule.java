package com.bachlinh.order.web.dto.rule;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateDeleteForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateDeleteRule extends AbstractRule<EmailTemplateDeleteForm> {
    private EmailTemplateRepository emailTemplateRepository;

    @ActiveReflection
    public EmailTemplateDeleteRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateDeleteForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.id())) {
            var key = "template_id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity template for delete", validateResult);
        }

        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (emailTemplateRepository.isEmailTemplateExisted(dto.id(), customer)) {
            var key = "template_id";
            RuntimeUtils.computeMultiValueMap(key, "Not found", validateResult);
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
    }

    @Override
    public Class<EmailTemplateDeleteForm> applyOnType() {
        return EmailTemplateDeleteForm.class;
    }
}
