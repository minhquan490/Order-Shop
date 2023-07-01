package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.OrderChangeStatusForm;
import com.bachlinh.order.web.service.common.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class OrderChangeStatusRule extends AbstractRule<OrderChangeStatusForm> {
    private OrderService orderService;

    @ActiveReflection
    public OrderChangeStatusRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(OrderChangeStatusForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        if (!StringUtils.hasText(dto.orderId())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Order id must not be empty", validateResult);
        }
        if (!StringUtils.hasText(dto.status())) {
            var key = "status";
            RuntimeUtils.computeMultiValueMap(key, "Order status must not be empty", validateResult);
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
        if (orderService == null) {
            orderService = getResolver().resolveDependencies(OrderService.class);
        }
    }

    @Override
    public Class<OrderChangeStatusForm> applyOnType() {
        return OrderChangeStatusForm.class;
    }
}
