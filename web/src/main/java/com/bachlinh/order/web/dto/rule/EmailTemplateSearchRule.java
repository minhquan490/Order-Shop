package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateSearchForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateSearchRule extends AbstractRule<EmailTemplateSearchForm> {

    @ActiveReflection
    public EmailTemplateSearchRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateSearchForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getQuery())) {
            var key = "query";
            RuntimeUtils.computeMultiValueMap(key, "Can not perform search for empty keyword", validationResult);
        }
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

    @Override
    protected void injectDependencies() {
        // Do nothing
    }

    @Override
    public Class<EmailTemplateSearchForm> applyOnType() {
        return EmailTemplateSearchForm.class;
    }
}
