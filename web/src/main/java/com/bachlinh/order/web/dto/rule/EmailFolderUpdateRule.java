package com.bachlinh.order.web.dto.rule;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.EmailFolderUpdateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailFolderUpdateRule extends AbstractRule<EmailFolderUpdateForm> {
    private EmailFoldersRepository emailFoldersRepository;

    @ActiveReflection
    public EmailFolderUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailFolderUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity email folder for update", validateResult);
        }

        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of email folder must not be empty", validateResult);
        }

        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (emailFoldersRepository.isFolderExisted(dto.getName(), customer)) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of email folder is existed", validateResult);
        }

        if (emailFoldersRepository.getEmailFolderById(dto.getId(), customer) == null) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Email folder not found", validateResult);
        }

        if (dto.getCleanPolicy() < -1) {
            dto.setCleanPolicy(-1);
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
        if (emailFoldersRepository == null) {
            emailFoldersRepository = getResolver().resolveDependencies(EmailFoldersRepository.class);
        }
    }

    @Override
    public Class<EmailFolderUpdateForm> applyOnType() {
        return EmailFolderUpdateForm.class;
    }
}
