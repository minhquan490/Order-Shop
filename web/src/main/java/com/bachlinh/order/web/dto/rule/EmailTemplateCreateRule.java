package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateCreateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateCreateRule extends AbstractRule<EmailTemplateCreateForm> {


    @ActiveReflection
    public EmailTemplateCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of template must not be empty", validateResult);
        }

        if (!StringUtils.hasText(dto.getTitle())) {
            var key = "title";
            RuntimeUtils.computeMultiValueMap(key, "Title of template must not be empty", validateResult);
        }

        if (!StringUtils.hasText(dto.getContent())) {
            var key = "content";
            RuntimeUtils.computeMultiValueMap(key, "Content of template must not be empty", validateResult);
        }

        if (dto.getParams() == null || dto.getParams().length == 0) {
            var key = "params";
            RuntimeUtils.computeMultiValueMap(key, "Params for template must not be specify", validateResult);
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
        // Do nothing
    }

    @Override
    public Class<EmailTemplateCreateForm> applyOnType() {
        return EmailTemplateCreateForm.class;
    }
}
