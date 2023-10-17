package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.CategoryRepository;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.web.repository.spi.ProductRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.core.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.product.ProductUpdateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ActiveReflection
@DtoValidationRule
public class ProductUpdateRule extends AbstractRule<ProductUpdateForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String RANGE_INVALID_MESSAGE_ID = "MSG-000010";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String INVALID_MESSAGE_ID = "MSG-000009";
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";
    private static final String MUST_BE_NUMBER_MESSAGE_ID = "MSG-000018";
    private static final String ENABLE_DISABLE_INVALID_MESSAGE_ID = "MSG-000020";
    private static final String PRODUCT_MUST_HAVE_CATEGORY_MESSAGE_ID = "MSG-000028";
    private static final String PRODUCT_NAME_KEY = "product_name";
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private EntityFactory entityFactory;
    private MessageSettingRepository messageSettingRepository;

    private ProductUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<ProductUpdateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new ProductUpdateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(ProductUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting mustBeNumberMessage = messageSettingRepository.getMessageById(MUST_BE_NUMBER_MESSAGE_ID);
        MessageSetting invalidMessage = messageSettingRepository.getMessageById(INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(dto.getProductId())) {
            var key = "product_id";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "product", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            return createResult(validateResult);
        }

        Product old = productRepository.getProductForUpdate(dto.getProductId());

        if (!old.getName().equals(dto.getProductName())) {
            validateProductName(dto.getProductName(), validateResult, nonEmptyMessage);
        }

        if (!old.getPrice().toString().equals(dto.getProductPrice())) {
            validateProductPrice(dto.getProductPrice(), validateResult, nonEmptyMessage, mustBeNumberMessage);
        }

        if (!old.getSize().equals(dto.getProductSize())) {
            validateProductSize(dto.getProductSize(), validateResult, nonEmptyMessage, invalidMessage);
        }

        if (!old.getColor().equals(dto.getProductColor())) {
            validateProductColor(dto.getProductColor(), validateResult, nonEmptyMessage);
        }

        if (!old.getTaobaoUrl().equals(dto.getProductTaobaoUrl())) {
            validateProductTaobaoUrl(dto.getProductTaobaoUrl(), validateResult, nonEmptyMessage, invalidMessage);
        }

        if (!String.valueOf(old.isEnabled()).equals(dto.getProductEnabled())) {
            validateProductEnable(dto.getProductEnabled(), validateResult);
        }

        if (!old.getOrderPoint().toString().equals(dto.getProductOrderPoint())) {
            validateProductOrderPoint(dto.getProductOrderPoint(), validateResult, nonEmptyMessage, mustBeNumberMessage);
        }

        validateProductCategories(dto.getProductCategoriesId(), validateResult, invalidMessage);

        return createResult(validateResult);
    }

    @Override
    protected void injectDependencies() {
        if (productRepository == null) {
            productRepository = resolveRepository(ProductRepository.class);
        }
        if (categoryRepository == null) {
            categoryRepository = resolveRepository(CategoryRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = getResolver().resolveDependencies(EntityFactory.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<ProductUpdateForm> applyOnType() {
        return ProductUpdateForm.class;
    }

    private ValidatedDto.ValidateResult createResult(Map<String, List<String>> validateResult) {
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

    private void validateProductName(String productName, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage) {
        if (!StringUtils.hasText(productName)) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Name of product");
            RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, errorContent, validateResult);
        } else {
            int productNameLength = productName.length();
            if (productNameLength < 4 || productNameLength > 32) {
                MessageSetting messageSetting = messageSettingRepository.getMessageById(RANGE_INVALID_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Product name", "4", "32");
                RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, errorContent, validateResult);
            }

            var product = entityFactory.getEntity(Product.class);
            product.setName(productName);
            if (productRepository.productNameExist(product)) {
                MessageSetting messageSetting = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Product name");
                RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, errorContent, validateResult);
            }
        }
    }

    private void validateProductPrice(String productPrice, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting mustBeNumberMessage) {
        if (!StringUtils.hasText(productPrice)) {
            var key = "product_price";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Price of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isNumber(productPrice)) {
                var key = "product_price";
                String errorContent = MessageFormat.format(mustBeNumberMessage.getValue(), "Price of product");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateProductSize(String productSize, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting invalidMessage) {
        if (!StringUtils.hasText(productSize)) {
            var key = "product_size";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Size of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isSizeValid(productSize)) {
                var key = "product_size";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Size of product");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateProductColor(String productColor, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage) {
        if (!StringUtils.hasText(productColor)) {
            var key = "product_color";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Color of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
    }

    private void validateProductTaobaoUrl(String taobaoUrl, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting invalidMessage) {
        if (!StringUtils.hasText(taobaoUrl)) {
            var key = "product_taobao_url";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Tabao url of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isUrlValid(taobaoUrl)) {
                var key = "product_taobao_url";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Taobao url of product");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateProductEnable(String productEnabled, Map<String, List<String>> validateResult) {
        if (!StringUtils.hasText(productEnabled)) {
            var key = "product_enabled";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(ENABLE_DISABLE_INVALID_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Product must");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
    }

    private void validateProductCategories(String[] productCategoriesId, Map<String, List<String>> validateResult, MessageSetting invalidMessage) {
        if (productCategoriesId.length == 0 || Stream.of(productCategoriesId).anyMatch(category -> !StringUtils.hasText(category))) {
            var key = "product_categories";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(PRODUCT_MUST_HAVE_CATEGORY_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap(key, messageSetting.getValue(), validateResult);
        } else {
            boolean categoryValidateResult = Stream.of(productCategoriesId).anyMatch(id -> !categoryRepository.isExits(id));
            if (categoryValidateResult) {
                var key = "product_categories";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Category of product");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateProductOrderPoint(String productOrderPoint, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting mustBeNumberMessage) {
        if (!StringUtils.hasText(productOrderPoint)) {
            var key = "product_order_point";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Point of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isNumber(productOrderPoint)) {
                var key = "product_order_point";
                String errorContent = MessageFormat.format(mustBeNumberMessage.getValue(), "Order point of product");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }
}
