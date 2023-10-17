package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.common.AddEmailToTrashForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
public class AddEmailToTrashRule extends AbstractRule<AddEmailToTrashForm> {
    private static final String UNKNOWN_ADD_MESSAGE_ID = "MSG-000025";

    private MessageSettingRepository messageSettingRepository;

    private AddEmailToTrashRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<AddEmailToTrashForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new AddEmailToTrashRule(environment, resolver);
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
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<AddEmailToTrashForm> applyOnType() {
        return AddEmailToTrashForm.class;
    }
}
