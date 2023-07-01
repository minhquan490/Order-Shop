package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.CartForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CartFormRule extends AbstractRule<CartForm> {
    private ProductRepository productRepository;

    @ActiveReflection
    public CartFormRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CartForm dto) {
        var validateResult = new HashMap<String, List<String>>(3);
        for (var productDto : dto.getProductForms()) {
            validateCommonCase(productDto, validateResult);

            if (!ValidateUtils.isNumber(productDto.amount())) {
                var key = "product.amount";
                RuntimeUtils.computeMultiValueMap(key, "Amount of product must be a number", validateResult);
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
    }

    @Override
    public Class<CartForm> applyOnType() {
        return CartForm.class;
    }

    private void validateCommonCase(CartForm.ProductForm dto, Map<String, List<String>> result) {
        if (!StringUtils.hasText(dto.id())) {
            var key = "product.id";
            RuntimeUtils.computeMultiValueMap(key, String.format("Can not identity product [%s]", dto.id()), result);
        }
        if (!StringUtils.hasText(dto.name())) {
            var key = "product.name";
            RuntimeUtils.computeMultiValueMap(key, "Name of product must not be empty", result);
        }
        if (!StringUtils.hasText(dto.amount())) {
            var key = "product.amount";
            RuntimeUtils.computeMultiValueMap(key, "Amount of product must not be empty", result);
        }
    }
}
