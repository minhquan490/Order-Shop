package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
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

        if (!StringUtils.hasText(dto.getProductColor())) {
            var key = "product_color";
            String errorContent = MessageFormat.format(nonEmptyMessage.getValue(), "Color of product");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

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

        if (!StringUtils.hasText(dto.getProductEnabled())) {
            var key = "product_enabled";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(ENABLE_DISABLE_INVALID_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Product must");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }

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
            productRepository = getResolver().resolveDependencies(ProductRepository.class);
        }
        if (categoryRepository == null) {
            categoryRepository = getResolver().resolveDependencies(CategoryRepository.class);
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
}
