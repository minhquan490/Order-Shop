package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = ProductMedia.class)
public class ProductMediaValidator extends AbstractValidator<ProductMedia> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";
    private static final String ASSOCIATE_INVALID_MESSAGE_ID = "MSG-000032";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(ProductMedia entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting nonNullMessage = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);
        MessageSetting associateInvalidMessage = messageSettingRepository.getMessageById(ASSOCIATE_INVALID_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getUrl())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Url of product media"));
        } else {
            if (entity.getUrl().length() > 100) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Url of product media", "100"));
            }
        }

        if (!StringUtils.hasText(entity.getContentType())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Content type of product media"));
        } else {
            if (entity.getContentType().length() > 100) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "Content type of product media", "100"));
            }
        }

        if (entity.getContentLength() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessage.getValue(), "Content length of product media"));
        }

        if (entity.getProduct() == null) {
            result.addMessageError(MessageFormat.format(associateInvalidMessage.getValue(), "Product", "this media"));
        }

        return result;
    }
}
