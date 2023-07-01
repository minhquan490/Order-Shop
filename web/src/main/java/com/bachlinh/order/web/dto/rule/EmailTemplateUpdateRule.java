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
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateUpdateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailTemplateUpdateRule extends AbstractRule<EmailTemplateUpdateForm> {
    private EmailTemplateFolderRepository emailTemplateFolderRepository;

    @ActiveReflection
    public EmailTemplateUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailTemplateUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity email template for update", validateResult);
        }

        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of email template must not be empty", validateResult);
        }

        if (!StringUtils.hasText(dto.getTitle())) {
            var key = "title";
            RuntimeUtils.computeMultiValueMap(key, "Title of email template must not be empty", validateResult);
        }

        if (!StringUtils.hasText(dto.getContent())) {
            var key = "content";
            RuntimeUtils.computeMultiValueMap(key, "Content of email template must not be empty", validateResult);
        }

        if (!StringUtils.hasText(dto.getFolderId())) {
            var key = "folder_id";
            RuntimeUtils.computeMultiValueMap(key, "Email template folder must not be empty", validateResult);
        }

        if (dto.getParams() == null || dto.getParams().length == 0) {
            var key = "params";
            RuntimeUtils.computeMultiValueMap(key, "Email template must have at least 1 param", validateResult);
        }

        var isExist = emailTemplateFolderRepository.isEmailTemplateFolderIdExisted(dto.getFolderId());
        if (isExist) {
            var key = "folder_id";
            RuntimeUtils.computeMultiValueMap(key, "Folder is not exist", validateResult);
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
    public Class<EmailTemplateUpdateForm> applyOnType() {
        return EmailTemplateUpdateForm.class;
    }
}
