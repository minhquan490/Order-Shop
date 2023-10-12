package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;

import java.text.MessageFormat;

import org.springframework.util.StringUtils;

@ActiveReflection
@ApplyOn(entity = Category.class)
public class CategoryValidator extends AbstractValidator<Category> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_MESSAGE_ID = "MSG-000002";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Category entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthMessage = messageSettingRepository.getMessageById(LENGTH_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getName())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Category name"));
        } else {
            if (entity.getName().length() > 60) {
                result.addMessageError(MessageFormat.format(lengthMessage.getValue(), "Category name"));
            }
        }
        return result;
    }
}
