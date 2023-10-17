package com.bachlinh.order.web.common.validator;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.web.repository.spi.ProductRepository;

import java.text.MessageFormat;
import java.util.Collection;

import org.springframework.util.StringUtils;

@ActiveReflection
@ApplyOn(entity = Product.class)
public class ProductValidator extends AbstractValidator<Product> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String NEGATIVE_MESSAGE_ID = "MSG-000013";
    private static final String PRODUCT_MISSING_CATEGORY_MESSAGE_ID = "MSG-000022";

    private ProductRepository productRepository;
    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (productRepository == null) {
            productRepository = resolveRepository(ProductRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Product entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);
        MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
        MessageSetting negativeMessage = messageSettingRepository.getMessageById(NEGATIVE_MESSAGE_ID);

        if (entity.isNew()) {
            validateName(entity.getName(), result, entity, nonEmptyMessage, lengthInvalidMessage, existedMessage);
            validateSize(entity.getSize(), result, nonEmptyMessage, lengthInvalidMessage);
            validateColor(entity.getColor(), result, nonEmptyMessage, lengthInvalidMessage);
            validatePrice(entity.getPrice(), result, negativeMessage);
            validateCategories(entity.getCategories(), result);
        } else {
            Product old = productRepository.getProductForUpdate(entity.getId());
            if (!old.getName().equals(entity.getName())) {
                validateName(entity.getName(), result, entity, nonEmptyMessage, lengthInvalidMessage, existedMessage);
            }
            if (!old.getSize().equals(entity.getSize())) {
                validateSize(entity.getSize(), result, nonEmptyMessage, lengthInvalidMessage);
            }
            if (!old.getColor().equals(entity.getColor())) {
                validateColor(entity.getColor(), result, nonEmptyMessage, lengthInvalidMessage);
            }
            if (!old.getPrice().equals(entity.getPrice())) {
                validatePrice(entity.getPrice(), result, negativeMessage);
            }
        }
        return result;
    }

    private void validateName(String name, ValidateResult result, Product entity, MessageSetting nonEmptyMessage, MessageSetting lengthInvalidMessage, MessageSetting existedMessage) {
        if (!StringUtils.hasText(name)) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Product name"));
        } else {
            if (name.length() > 100) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "product name", "100"));
            }
            if (productRepository.productNameExist(entity)) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), "Product name"));
            }
        }
    }

    private void validateSize(String size, ValidateResult result, MessageSetting nonEmptyMessage, MessageSetting lengthInvalidMessage) {
        if (!StringUtils.hasText(size)) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Product size"));
        } else {
            if (size.length() > 3) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "product size", "3"));
            }
        }
    }

    private void validateColor(String color, ValidateResult result, MessageSetting nonEmptyMessage, MessageSetting lengthInvalidMessage) {
        if (!StringUtils.hasText(color)) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Product color"));
        } else {
            if (color.length() > 30) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "product color", "30"));
            }
        }
    }

    private void validatePrice(Integer price, ValidateResult result, MessageSetting negativeMessage) {
        if (price < 0) {
            result.addMessageError(MessageFormat.format(negativeMessage.getValue(), "Price"));
        }
    }

    private void validateCategories(Collection<Category> categories, ValidateResult result) {
        if (categories.isEmpty()) {
            MessageSetting productMissingCategoryMessage = messageSettingRepository.getMessageById(PRODUCT_MISSING_CATEGORY_MESSAGE_ID);
            result.addMessageError(productMissingCategoryMessage.getValue());
        }
    }
}
