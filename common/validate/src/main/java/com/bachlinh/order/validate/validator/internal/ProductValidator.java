package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
public class ProductValidator extends AbstractValidator<Product> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String NEGATIVE_MESSAGE_ID = "MSG-000013";
    private static final String PRODUCT_MISSING_CATEGORY_MESSAGE_ID = "MSG-000022";

    private ProductRepository productRepository;
    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public ProductValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        if (productRepository == null) {
            productRepository = getResolver().resolveDependencies(ProductRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Product entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessage = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);
        MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
        MessageSetting negativeMessage = messageSettingRepository.getMessageById(NEGATIVE_MESSAGE_ID);
        MessageSetting productMissingCategoryMessage = messageSettingRepository.getMessageById(PRODUCT_MISSING_CATEGORY_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getName())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Product name"));
        } else {
            if (entity.getName().length() > 100) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "product name", "100"));
            }
            if (productRepository.productNameExist(entity)) {
                result.addMessageError(MessageFormat.format(existedMessage.getValue(), "Product name"));
            }
        }

        if (!StringUtils.hasText(entity.getSize())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Product size"));
        } else {
            if (entity.getSize().length() > 3) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "product size", "3"));
            }
        }

        if (!StringUtils.hasText(entity.getColor())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Product color"));
        } else {
            if (entity.getColor().length() > 30) {
                result.addMessageError(MessageFormat.format(lengthInvalidMessage.getValue(), "product color", "30"));
            }
        }

        if (entity.getPrice() < 0) {
            result.addMessageError(MessageFormat.format(negativeMessage.getValue(), "Price"));
        }

        if (entity.getCategories().isEmpty()) {
            result.addMessageError(productMissingCategoryMessage.getValue());
        }
        return result;
    }
}
