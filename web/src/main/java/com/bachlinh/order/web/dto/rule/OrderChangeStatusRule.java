package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.order.OrderChangeStatusForm;
import com.bachlinh.order.web.service.common.OrderService;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class OrderChangeStatusRule extends AbstractRule<OrderChangeStatusForm> {
    private static final String NOT_EMPTY_MESSAGE_ID = "MSG-000001";

    private OrderService orderService;
    private MessageSettingRepository messageSettingRepository;

    private OrderChangeStatusRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<OrderChangeStatusForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new OrderChangeStatusRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(OrderChangeStatusForm dto) {
        var validateResult = new HashMap<String, List<String>>();

        MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_EMPTY_MESSAGE_ID);

        if (!StringUtils.hasText(dto.orderId())) {
            var key = "id";
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Order id");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
        }
        if (!StringUtils.hasText(dto.status())) {
            var key = "status";
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Order status");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validateResult);
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
            orderService = resolveRepository(OrderService.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<OrderChangeStatusForm> applyOnType() {
        return OrderChangeStatusForm.class;
    }
}
