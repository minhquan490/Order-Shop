package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.AddEmailToTrashForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
public class AddEMailToTrashRule extends AbstractRule<AddEmailToTrashForm> {
    private static final String UNKNOWN_ADD_MESSAGE_ID = "MSG-000025";

    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public AddEMailToTrashRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(AddEmailToTrashForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting unknownAddMessage = messageSettingRepository.getMessageById(UNKNOWN_ADD_MESSAGE_ID);

        if (dto.getEmailIds().length == 0) {
            var key = "email_ids";
            RuntimeUtils.computeMultiValueMap(key, unknownAddMessage.getValue(), validateResult);
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
    public Class<AddEmailToTrashForm> applyOnType() {
        return AddEmailToTrashForm.class;
    }
}