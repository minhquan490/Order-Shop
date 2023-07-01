package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.EmailTemplateFolderUpdateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateFolderUpdateRule extends AbstractRule<EmailTemplateFolderUpdateForm> {

    @ActiveReflection
    public EmailTemplateFolderUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateFolderUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.id())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity template folder for update", validateResult);
        }

        if (!StringUtils.hasText(dto.name())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of template folder must not be empty", validateResult);
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
    public Class<EmailTemplateFolderUpdateForm> applyOnType() {
        return EmailTemplateFolderUpdateForm.class;
    }
}
