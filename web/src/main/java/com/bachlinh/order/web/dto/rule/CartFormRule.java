package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.customer.CartForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class CartFormRule extends AbstractRule<CartForm> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String NOT_FOUND_MESSAGE_ID = "MSG-000008";
    private static final String NON_IDENTITY_MESSAGE_ID = "MSG-000011";
    private static final String NON_NUMBER_MESSAGE_ID = "MSG-000018";

    private ProductRepository productRepository;
    private MessageSettingRepository messageSettingRepository;

    private CartFormRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<CartForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new CartFormRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(CartForm dto) {
        HashMap<String, List<String>> validateResult = HashMap.newHashMap(3);
        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);

        if (dto.getProductForms().length == 0) {
            RuntimeUtils.computeMultiValueMap("products", MessageFormat.format(nonEmptyMessage.getValue(), "Product to add to cart"), validateResult);
            return createResult(validateResult);
        }

        MessageSetting nonNumberMessage = messageSettingRepository.getMessageById(NON_NUMBER_MESSAGE_ID);

        for (var productDto : dto.getProductForms()) {
            if (!StringUtils.hasText(productDto.id())) {
                MessageSetting nonIdentityMessage = messageSettingRepository.getMessageById(NON_IDENTITY_MESSAGE_ID);
                RuntimeUtils.computeMultiValueMap("product.id", MessageFormat.format(nonIdentityMessage.getValue(), "product", productDto.id(), "add to cart"), validateResult);
                return createResult(validateResult);
            } else {
                if (productRepository.isProductExist(productDto.id())) {
                    MessageSetting notFoundMessage = messageSettingRepository.getMessageById(NOT_FOUND_MESSAGE_ID);
                    RuntimeUtils.computeMultiValueMap("product.id", MessageFormat.format(notFoundMessage.getValue(), "Product"), validateResult);
                }
            }

            var key = "product.amount";
            if (!StringUtils.hasText(productDto.amount())) {
                RuntimeUtils.computeMultiValueMap(key, MessageFormat.format(nonEmptyMessage.getValue(), "Amount of product"), validateResult);
            } else {
                if (!ValidateUtils.isNumber(productDto.amount())) {
                    RuntimeUtils.computeMultiValueMap(key, MessageFormat.format(nonNumberMessage.getValue(), "Amount of product"), validateResult);
                }
            }
        }
        return createResult(validateResult);
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
    public Class<CartForm> applyOnType() {
        return CartForm.class;
    }

    private ValidatedDto.ValidateResult createResult(Map<String, List<String>> result) {
        return new ValidatedDto.ValidateResult() {
            @Override
            public Map<String, Object> getErrorResult() {
                return new HashMap<>(result);
            }

            @Override
            public boolean shouldHandle() {
                return result.isEmpty();
            }
        };
    }
}
