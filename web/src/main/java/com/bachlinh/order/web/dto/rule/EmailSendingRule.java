package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.email.sending.NormalEmailSendingForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class EmailSendingRule extends AbstractRule<NormalEmailSendingForm> {
    private static final String EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String SPECIFY_MESSAGE_ID = "MSG-000014";

    private MessageSettingRepository messageSettingRepository;

    public EmailSendingRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(NormalEmailSendingForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting emptyMessage = messageSettingRepository.getMessageById(EMPTY_MESSAGE_ID);

        if (!StringUtils.hasText(dto.getContent())) {
            var key = "content";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Email content");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getTitle())) {
            var key = "title";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Email title");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

        if (!StringUtils.hasText(dto.getToCustomer())) {
            var key = "to";
            MessageSetting specifyMessage = messageSettingRepository.getMessageById(SPECIFY_MESSAGE_ID);
            String errorMessage = MessageFormat.format(specifyMessage.getValue(), "Receiver");
            RuntimeUtils.computeMultiValueMap(key, errorMessage, validateResult);
        }

        if (!StringUtils.hasText(dto.getContentType())) {
            var key = "content_type";
            String errorContent = MessageFormat.format(emptyMessage.getValue(), "Content type of email");
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
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<NormalEmailSendingForm> applyOnType() {
        return NormalEmailSendingForm.class;
    }
}
