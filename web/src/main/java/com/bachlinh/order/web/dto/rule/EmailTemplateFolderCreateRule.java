package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderCreateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateFolderCreateRule extends AbstractRule<EmailTemplateFolderCreateForm> {
    private EmailTemplateFolderRepository emailTemplateFolderRepository;

    @ActiveReflection
    public EmailTemplateFolderCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateFolderCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.name())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of template folder must not be empty", validateResult);
        }

        var isExisted = emailTemplateFolderRepository.isEmailTemplateFolderNameExisted(dto.name());
        if (isExisted) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of template folder is existed", validateResult);
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
    }

    @Override
    public Class<EmailTemplateFolderCreateForm> applyOnType() {
        return EmailTemplateFolderCreateForm.class;
    }
}
