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
import com.bachlinh.order.web.dto.form.common.EmailFolderCreateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailFolderCreateRule extends AbstractRule<EmailFolderCreateForm> {
    private EmailFoldersRepository emailFoldersRepository;

    @ActiveReflection
    public EmailFolderCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailFolderCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>(2);
        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of email folder must not be empty", validateResult);
        }
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (emailFoldersRepository.isFolderExisted(dto.getName(), customer)) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of folder is existed", validateResult);
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
    public Class<EmailFolderCreateForm> applyOnType() {
        return EmailFolderCreateForm.class;
    }
}
