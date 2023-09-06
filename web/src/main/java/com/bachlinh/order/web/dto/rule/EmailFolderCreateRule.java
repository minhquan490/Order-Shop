package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.EmailFolderCreateForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailFolderCreateRule extends AbstractRule<EmailFolderCreateForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXITED_MESSAGE_ID = "MSG-000007";

    private EmailFoldersRepository emailFoldersRepository;
    private MessageSettingRepository messageSettingRepository;

    private EmailFolderCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailFolderCreateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailFolderCreateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailFolderCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>(2);
        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Name of email folder");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (emailFoldersRepository.isFolderExisted(dto.getName(), customer)) {
            var key = "name";
            MessageSetting exitedMessage = messageSettingRepository.getMessageById(EXITED_MESSAGE_ID);
            String errorContent = MessageFormat.format(exitedMessage.getValue(), "Name of folder");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
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
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailFolderCreateForm> applyOnType() {
        return EmailFolderCreateForm.class;
    }
}
