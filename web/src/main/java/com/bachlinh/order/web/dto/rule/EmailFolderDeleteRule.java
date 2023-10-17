package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.EmailFoldersRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.EmailFolderDeleteForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailFolderDeleteRule extends AbstractRule<EmailFolderDeleteForm> {
    private static final String NOT_FOUND_MESSAGE_ID = "MSG-000008";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";

    private EmailFoldersRepository emailFoldersRepository;
    private MessageSettingRepository messageSettingRepository;

    private EmailFolderDeleteRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailFolderDeleteForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailFolderDeleteRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailFolderDeleteForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            MessageSetting canNotIdentityMessage = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(canNotIdentityMessage.getValue(), "email folder", "", "delete");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
        } else {
            var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (emailFoldersRepository.getEmailFolderById(dto.getId(), customer) == null) {
                var key = "id";
                MessageSetting notFoundMessage = messageSettingRepository.getMessageById(NOT_FOUND_MESSAGE_ID);
                String errorContent = MessageFormat.format(notFoundMessage.getValue(), "Email folder");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
            }
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
        if (emailFoldersRepository == null) {
            emailFoldersRepository = resolveRepository(EmailFoldersRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailFolderDeleteForm> applyOnType() {
        return EmailFolderDeleteForm.class;
    }
}
