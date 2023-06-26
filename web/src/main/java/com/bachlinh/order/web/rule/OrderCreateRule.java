package com.bachlinh.order.web.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.customer.OrderCreateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class OrderCreateRule extends AbstractRule<OrderCreateForm> {
    private ProductRepository productRepository;

    @ActiveReflection
    public OrderCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(OrderCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        var details = dto.getDetails();
        var idKey = "detail.product_id";
        var amountKey = "detail.amount";
        var productNameKey = "detail.product_name";
        for (var detail : details) {
            if (!StringUtils.hasText(detail.getProductName())) {
                RuntimeUtils.computeMultiValueMap(productNameKey, "Product name must not be empty", validateResult);
            }
            if (!StringUtils.hasText(detail.getProductId()) || !productRepository.isProductExist(detail.getProductId())) {
                RuntimeUtils.computeMultiValueMap(idKey, String.format("Product [%s] is not existed", detail.getProductName()), validateResult);
            }
            if (!StringUtils.hasText(detail.getAmount())) {
                RuntimeUtils.computeMultiValueMap(amountKey, "Product amount must not be empty", validateResult);
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
    public Class<OrderCreateForm> applyOnType() {
        return OrderCreateForm.class;
    }
}
