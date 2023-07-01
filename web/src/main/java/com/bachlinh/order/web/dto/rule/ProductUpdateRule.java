package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.ProductUpdateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ActiveReflection
@DtoValidationRule
public class ProductUpdateRule extends AbstractRule<ProductUpdateForm> {
    private static final String PRODUCT_NAME_KEY = "product_name";
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private EntityFactory entityFactory;

    @ActiveReflection
    public ProductUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(ProductUpdateForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        validateCommonCase(dto, validateResult);
        validateLength(dto, validateResult);
        validateNumber(dto, validateResult);
        validateRegex(dto, validateResult);
        validateExist(dto, validateResult);
        return null;
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
    }

    @Override
    public Class<ProductUpdateForm> applyOnType() {
        return ProductUpdateForm.class;
    }

    private void validateCommonCase(ProductUpdateForm dto, Map<String, List<String>> result) {
        if (!StringUtils.hasText(dto.getProductId())) {
            var key = "product_id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity product for update", result);
        }

        if (!StringUtils.hasText(dto.getProductName())) {
            RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, "Name of product must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getProductPrice())) {
            var key = "product_price";
            RuntimeUtils.computeMultiValueMap(key, "Price of product must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getProductSize())) {
            var key = "product_size";
            RuntimeUtils.computeMultiValueMap(key, "Size of product must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getProductColor())) {
            var key = "product_color";
            RuntimeUtils.computeMultiValueMap(key, "Color of product must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getProductTaobaoUrl())) {
            var key = "product_taobao_url";
            RuntimeUtils.computeMultiValueMap(key, "Taobao url of product must not be empty", result);
        }

        if (!StringUtils.hasText(dto.getProductEnabled())) {
            var key = "product_enabled";
            RuntimeUtils.computeMultiValueMap(key, "Product must be enable of disable", result);
        }

        if (dto.getProductCategoriesId().length == 0 || Stream.of(dto.getProductCategoriesId()).anyMatch(category -> !StringUtils.hasText(category))) {
            var key = "product_categories";
            RuntimeUtils.computeMultiValueMap(key, "Product must have category", result);
        }

        if (!StringUtils.hasText(dto.getProductOrderPoint())) {
            var key = "product_order_point";
            RuntimeUtils.computeMultiValueMap(key, "Point of product must not be empty", result);
        }
    }

    private void validateLength(ProductUpdateForm dto, Map<String, List<String>> result) {
        int productNameLength = dto.getProductName().length();
        if (productNameLength < 4 || productNameLength > 32) {
            RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, "Product name must be in range 4 - 32", result);
        }
    }

    private void validateNumber(ProductUpdateForm dto, Map<String, List<String>> result) {
        if (!ValidateUtils.isNumber(dto.getProductPrice())) {
            var key = "product_price";
            RuntimeUtils.computeMultiValueMap(key, "Price of product must be a number", result);
        }
        if (!ValidateUtils.isNumber(dto.getProductOrderPoint())) {
            var key = "product_order_point";
            RuntimeUtils.computeMultiValueMap(key, "Order point of product must be number", result);
        }
    }

    private void validateRegex(ProductUpdateForm dto, Map<String, List<String>> result) {
        if (!ValidateUtils.isSizeValid(dto.getProductSize())) {
            var key = "product_size";
            RuntimeUtils.computeMultiValueMap(key, "Size of product is invalid", result);
        }

        if (!ValidateUtils.isUrlValid(dto.getProductTaobaoUrl())) {
            var key = "product_taobao_url";
            RuntimeUtils.computeMultiValueMap(key, "Taobao url is invalid", result);
        }
    }

    private void validateExist(ProductUpdateForm dto, Map<String, List<String>> result) {
        var product = entityFactory.getEntity(Product.class);
        product.setName(dto.getProductName());

        if (productRepository.productNameExist(product)) {
            RuntimeUtils.computeMultiValueMap(PRODUCT_NAME_KEY, "Product name is existed", result);
        }

        boolean categoryValidateResult = Stream.of(dto.getProductCategoriesId()).anyMatch(id -> !categoryRepository.isExits(id));
        if (categoryValidateResult) {
            var key = "product_categories";
            RuntimeUtils.computeMultiValueMap(key, "One of category is invalid", result);
        }
    }
}
