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
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderDeleteForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateFolderDeleteRule extends AbstractRule<EmailTemplateFolderDeleteForm> {
    private EmailTemplateFolderRepository repository;

    @ActiveReflection
    public EmailTemplateFolderDeleteRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateFolderDeleteForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.id())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity email template folder for delete", validateResult);
        }

        if (repository.isEmailTemplateFolderIdExisted(dto.id())) {
            var key = "id";
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
        if (repository == null) {
            repository = getResolver().resolveDependencies(EmailTemplateFolderRepository.class);
        }
    }

    @Override
    public Class<EmailTemplateFolderDeleteForm> applyOnType() {
        return EmailTemplateFolderDeleteForm.class;
    }
}
