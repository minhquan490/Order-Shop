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
import com.bachlinh.order.web.dto.form.common.DeleteEmailInTrashForm;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class DeleteEmailInTrashRule extends AbstractRule<DeleteEmailInTrashForm> {
    private static final String AT_LEAST_MESSAGE_ID = "MSG-000015";

    private MessageSettingRepository messageSettingRepository;
    
    private DeleteEmailInTrashRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<DeleteEmailInTrashForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new DeleteEmailInTrashRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(DeleteEmailInTrashForm dto) {
        var validationResult = new HashMap<String, List<String>>(1);
        if (dto.getEmailId().length == 0) {
            var key = "email_ids";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(AT_LEAST_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Email ids");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
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
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<DeleteEmailInTrashForm> applyOnType() {
        return DeleteEmailInTrashForm.class;
    }
}
