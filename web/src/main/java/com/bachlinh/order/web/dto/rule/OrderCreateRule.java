package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.web.repository.spi.ProductRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.customer.OrderCreateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class OrderCreateRule extends AbstractRule<OrderCreateForm> {
    private static final String NOT_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String NOT_EXISTED_MESSAGE_ID = "MSG-000017";

    private ProductRepository productRepository;
    private MessageSettingRepository messageSettingRepository;

    private OrderCreateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<OrderCreateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new OrderCreateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(OrderCreateForm dto) {
        var validateResult = new HashMap<String, List<String>>();
        var details = dto.getDetails();
        var idKey = "detail.product_id";
        var amountKey = "detail.amount";
        var productNameKey = "detail.product_name";
        var notEmptyMessage = messageSettingRepository.getMessageById(NOT_EMPTY_MESSAGE_ID);
        for (var detail : details) {
            if (!StringUtils.hasText(detail.getProductName())) {
                String errorContent = MessageFormat.format(notEmptyMessage.getValue(), "Product name");
                RuntimeUtils.computeMultiValueMap(productNameKey, errorContent, validateResult);
            }
            if (!StringUtils.hasText(detail.getProductId()) || !productRepository.isProductExist(detail.getProductId())) {
                MessageSetting notExistedMessage = messageSettingRepository.getMessageById(NOT_EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(notExistedMessage.getValue(), String.format("Product [%s]", detail.getProductName()));
                RuntimeUtils.computeMultiValueMap(idKey, errorContent, validateResult);
            }
            if (!StringUtils.hasText(detail.getAmount())) {
                String errorContent = MessageFormat.format(notEmptyMessage.getValue(), "Product amount");
                RuntimeUtils.computeMultiValueMap(amountKey, errorContent, validateResult);
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
            productRepository = resolveRepository(ProductRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<OrderCreateForm> applyOnType() {
        return OrderCreateForm.class;
    }
}
