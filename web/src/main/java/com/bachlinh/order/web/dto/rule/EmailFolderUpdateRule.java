package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.EmailFolderUpdateForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailFolderUpdateRule extends AbstractRule<EmailFolderUpdateForm> {
    private static final String EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String NOT_FOUND_MESSAGE_ID = "MSG-000008";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";

    private EmailFoldersRepository emailFoldersRepository;
    private MessageSettingRepository messageSettingRepository;

    private EmailFolderUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<EmailFolderUpdateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new EmailFolderUpdateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailFolderUpdateForm dto) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var validateResult = new HashMap<String, List<String>>();

        validateId(dto.getId(), customer, validateResult);

        EmailFolders emailFolders = emailFoldersRepository.getEmailFolderById(dto.getId(), customer);

        if (!emailFolders.getName().equals(dto.getName())) {
            validateName(dto.getName(), customer, validateResult);
        }

        if (!emailFolders.getEmailClearPolicy().equals(dto.getCleanPolicy()) && dto.getCleanPolicy() < -1) {
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
            emailFoldersRepository = resolveRepository(EmailFoldersRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailFolderUpdateForm> applyOnType() {
        return EmailFolderUpdateForm.class;
    }

    private void validateId(String emailFolderId, Customer customer, Map<String, List<String>> validateResult) {
        var key = "id";
        if (!StringUtils.hasText(emailFolderId)) {
            MessageSetting canNotIdentityMessage = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(canNotIdentityMessage.getValue(), "email folder", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (emailFoldersRepository.getEmailFolderById(emailFolderId, customer) == null) {
                MessageSetting notFoundMessage = messageSettingRepository.getMessageById(NOT_FOUND_MESSAGE_ID);
                String errorContent = MessageFormat.format(notFoundMessage.getValue(), "Email folder");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateName(String folderName, Customer customer, Map<String, List<String>> validateResult) {
        var key = "name";
        if (!StringUtils.hasText(folderName)) {
            MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(EMPTY_MESSAGE_ID);
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Name of email folder");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (emailFoldersRepository.isFolderExisted(folderName, customer)) {
                MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(existedMessage.getValue(), "Name of email folder");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }
}
