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
import com.bachlinh.order.web.dto.form.admin.product.ProductCreateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ActiveReflection
@DtoValidationRule
public class ProductCreateRule extends AbstractRule<ProductCreateForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String INVALID_MESSAGE_ID = "MSG-000009";
    private static final String RANGE_INVALID_MESSAGE_ID = "MSG-000010";
    private static final String NON_NUMBER_MESSAGE_ID = "MSG-000018";
    private static final String ENABLE_DISABLE_INVALID_MESSAGE_ID = "MSG-000020";
    private static final String PRODUCT_MUST_HAVE_CATEGORY_MESSAGE_ID = "MSG-000028";
    private static final String PRODUCT_NAME_KEY = "product_name";
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private EntityFactory entityFactory;
    private MessageSettingRepository messageSettingRepository;

    private ProductCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<ProductCreateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new ProductCreateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(ProductCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting nonNumberMessage = messageSettingRepository.getMessageById(NON_NUMBER_MESSAGE_ID);
        MessageSetting invalidMessage = messageSettingRepository.getMessageById(INVALID_MESSAGE_ID);

        validateProductName(dto, validateResult, nonEmptyMessage);

        validateProductPrice(dto, validateResult, nonEmptyMessage, nonNumberMessage);

        validateProductSize(dto, validateResult, nonEmptyMessage, invalidMessage);

        validateProductColor(dto, validateResult, nonEmptyMessage);

        validateProductTaobaoUrl(dto, validateResult, nonEmptyMessage, invalidMessage);

        validateProductEnabled(dto, validateResult);

        validateProductCategory(dto, validateResult, invalidMessage);

        validateProductOrderPoint(dto, validateResult, nonEmptyMessage, nonNumberMessage);

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
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<ProductCreateForm> applyOnType() {
        return ProductCreateForm.class;
    }

    private void validateProductName(ProductCreateForm dto, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage) {
        if (!StringUtils.hasText(dto.getProductName())) {
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Name of product");
            RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, errorContent, validateResult);
        } else {
            int productNameLength = dto.getProductName().length();
            if (productNameLength < 4 || productNameLength > 32) {
                MessageSetting messageSetting = messageSettingRepository.getMessageById(RANGE_INVALID_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Product name", "4", "32");
                RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, errorContent, validateResult);
            }

            var product = entityFactory.getEntity(Product.class);
            product.setName(dto.getProductName());
            if (productRepository.productNameExist(product)) {
                MessageSetting existedMessage = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(existedMessage.getValue(), "Product name");
                RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, errorContent, validateResult);
            }
        }
    }

    private void validateProductPrice(ProductCreateForm dto, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting nonNumberMessage) {
        if (!StringUtils.hasText(dto.getProductPrice())) {
            var key = "product_price";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Price of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isNumber(dto.getProductPrice())) {
                var key = "product_price";
                String errorContent = MessageFormat.format(nonNumberMessage.getValue(), "Price of product");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateProductSize(ProductCreateForm dto, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting invalidMessage) {
        if (!StringUtils.hasText(dto.getProductSize())) {
            var key = "product_size";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Size of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isSizeValid(dto.getProductSize())) {
                var key = "product_size";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Size of product");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateProductColor(ProductCreateForm dto, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage) {
        if (!StringUtils.hasText(dto.getProductColor())) {
            var key = "product_color";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Color of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
    }

    private void validateProductTaobaoUrl(ProductCreateForm dto, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting invalidMessage) {
        if (!StringUtils.hasText(dto.getProductTaobaoUrl())) {
            var key = "product_taobao_url";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Tabao url of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isUrlValid(dto.getProductTaobaoUrl())) {
                var key = "product_taobao_url";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Tabao url");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateProductEnabled(ProductCreateForm dto, Map<String, List<String>> validateResult) {
        if (!StringUtils.hasText(dto.getProductEnabled())) {
            var key = "product_enabled";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(ENABLE_DISABLE_INVALID_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Product must");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
    }

    private void validateProductCategory(ProductCreateForm dto, Map<String, List<String>> validateResult, MessageSetting invalidMessage) {
        if (dto.getProductCategoriesId().length == 0 || Stream.of(dto.getProductCategoriesId()).anyMatch(category -> !StringUtils.hasText(category))) {
            var key = "product_categories";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(PRODUCT_MUST_HAVE_CATEGORY_MESSAGE_ID);
            RuntimeUtils.computeMultiValueMap(key, messageSetting.getValue(), validateResult);
        } else {
            boolean categoryValidateResult = Stream.of(dto.getProductCategoriesId()).anyMatch(id -> !categoryRepository.isExits(id));
            if (categoryValidateResult) {
                var key = "product_categories";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "One of category");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }

    private void validateProductOrderPoint(ProductCreateForm dto, Map<String, List<String>> validateResult, MessageSetting nonEmptyMessage, MessageSetting nonNumberMessage) {
        if (!StringUtils.hasText(dto.getProductOrderPoint())) {
            var key = "product_order_point";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Point of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        } else {
            if (!ValidateUtils.isNumber(dto.getProductOrderPoint())) {
                var key = "product_order_point";
                String errorContent = MessageFormat.format(nonNumberMessage.getValue(), "Order point of product");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
            }
        }
    }
}
