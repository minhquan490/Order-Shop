package com.bachlinh.order.web.common.validator;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = EmailTrash.class)
public class EmailTrashValidator extends AbstractValidator<EmailTrash> {
    private static final String ASSOCIATE_INVALID_MESSAGE_ID = "MSG-000032";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(EmailTrash entity) {
        Result result = new Result();

        MessageSetting associateInvalidMessage = messageSettingRepository.getMessageById(ASSOCIATE_INVALID_MESSAGE_ID);

        if (entity.getCustomer() == null) {
            result.addMessageError(MessageFormat.format(associateInvalidMessage.getValue(), "Customer", "email trash"));
        }

        return result;
    }
}
