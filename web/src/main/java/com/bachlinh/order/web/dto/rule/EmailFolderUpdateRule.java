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

    @ActiveReflection
    public EmailFolderUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(EmailFolderUpdateForm dto) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var validateResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            MessageSetting canNotIdentityMessage = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(canNotIdentityMessage.getValue(), "email folder", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (emailFoldersRepository.getEmailFolderById(dto.getId(), customer) == null) {
                var key = "id";
                MessageSetting notFoundMessage = messageSettingRepository.getMessageById(NOT_FOUND_MESSAGE_ID);
                String errorContent = MessageFormat.format(notFoundMessage.getValue(), "Email folder");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }

        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(EMPTY_MESSAGE_ID);
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Name of email folder");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (emailFoldersRepository.isFolderExisted(dto.getName(), customer)) {
                var key = "name";
                MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(existedMessage.getValue(), "Name of email folder");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
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
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<EmailFolderUpdateForm> applyOnType() {
        return EmailFolderUpdateForm.class;
    }
}