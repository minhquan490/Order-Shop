package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.VoucherRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherUpdateForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class VoucherUpdateRule extends AbstractRule<VoucherUpdateForm> {
    private static final String NOT_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String EXISTED_MESSAGE_ID = "MSG-000007";
    private static final String NOT_FOUND_MESSAGE_ID = "MSG-000008";
    private static final String INVALID_MESSAGE_ID = "MSG-000009";
    private static final String NOT_IDENTITY_MESSAGE_ID = "MSG-000011";
    private static final String POSITIVE_MESSAGE_ID = "MSG-000012";

    private VoucherRepository voucherRepository;
    private MessageSettingRepository messageSettingRepository;

    private VoucherUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<VoucherUpdateForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new VoucherUpdateRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(VoucherUpdateForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        validateVoucherId(dto.getId(), validationResult);

        if (!validationResult.isEmpty()) {
            return createResult(validationResult);
        }

        Voucher targetVoucher = voucherRepository.getVoucherForUpdate(dto.getId());

        if (!targetVoucher.getName().equals(dto.getName())) {
            validateVoucherName(dto.getName(), validationResult);
        }

        if (!targetVoucher.getDiscountPercent().equals(dto.getDiscountPercent())) {
            validateDiscountPercent(dto.getDiscountPercent(), validationResult);
        }

        validateTimeStart(dto.getTimeStart(), validationResult);

        validateTimeEnd(dto.getTimeEnd(), validationResult);

        if (!targetVoucher.getVoucherContent().equals(dto.getContent())) {
            validateContent(dto.getContent(), validationResult);
        }

        if (!targetVoucher.getVoucherCost().equals(dto.getCost())) {
            validateCost(dto.getCost(), validationResult);
        }

        return createResult(validationResult);
    }

    @Override
    protected void injectDependencies() {
        if (voucherRepository == null) {
            voucherRepository = getResolver().resolveDependencies(VoucherRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<VoucherUpdateForm> applyOnType() {
        return VoucherUpdateForm.class;
    }

    private ValidatedDto.ValidateResult createResult(Map<String, List<String>> validationResult) {
        return new ValidatedDto.ValidateResult() {
            @Override
            public Map<String, Object> getErrorResult() {
                return new HashMap<>(validationResult);
            }

            @Override
            public boolean shouldHandle() {
                return validationResult.isEmpty();
            }
        };
    }

    private void validateVoucherId(String voucherId, Map<String, List<String>> validationResult) {
        var key = "id";
        if (!StringUtils.hasText(voucherId)) {
            computedError(NOT_IDENTITY_MESSAGE_ID, key, validationResult, "voucher", "", "update");
        } else {
            if (!voucherRepository.isVoucherIdExist(voucherId)) {
                computedError(NOT_FOUND_MESSAGE_ID, key, validationResult, "Voucher");
            }
        }
    }

    private void validateVoucherName(String name, Map<String, List<String>> validationResult) {
        var key = "name";
        if (!StringUtils.hasText(name)) {
            computedError(NOT_EMPTY_MESSAGE_ID, key, validationResult, "Name of voucher");
        } else {
            if (voucherRepository.isVoucherNameExist(name)) {
                computedError(EXISTED_MESSAGE_ID, key, validationResult, "Name of voucher");
            }
        }
    }

    private void validateDiscountPercent(int discountPercent, Map<String, List<String>> validationResult) {
        var key = "discount_percent";
        if (discountPercent < 0) {
            computedError(POSITIVE_MESSAGE_ID, key, validationResult, "Discount percent");
        }
    }

    private void validateTimeStart(String timeStart, Map<String, List<String>> validationResult) {
        var key = "time_start";
        if (!StringUtils.hasText(timeStart)) {
            computedError(NOT_EMPTY_MESSAGE_ID, key, validationResult, "Time voucher begin");
        } else {
            if (!ValidateUtils.isValidDate(timeStart)) {
                computedError(INVALID_MESSAGE_ID, key, validationResult, "Time start of voucher");
            }
        }
    }

    private void validateTimeEnd(String timeEnd, Map<String, List<String>> validationResult) {
        var key = "time_end";
        if (!StringUtils.hasText(timeEnd)) {
            computedError(NOT_EMPTY_MESSAGE_ID, key, validationResult, "Time voucher end");
        } else {
            if (!ValidateUtils.isValidDate(timeEnd)) {
                computedError(INVALID_MESSAGE_ID, key, validationResult, "Time end of voucher");
            }
        }
    }

    private void validateContent(String content, Map<String, List<String>> validationResult) {
        var key = "content";
        if (!StringUtils.hasText(content)) {
            computedError(NOT_EMPTY_MESSAGE_ID, key, validationResult, "Content of voucher");
        }
    }

    private void validateCost(int cost, Map<String, List<String>> validationResult) {
        var key = "cost";
        if (cost < 0) {
            computedError(POSITIVE_MESSAGE_ID, key, validationResult, "Cost of voucher");
        }
    }

    private void computedError(String messageId, String key, Map<String, List<String>> validationResult, Object... params) {
        MessageSetting messageSetting = messageSettingRepository.getMessageById(messageId);
        String errorContent = MessageFormat.format(messageSetting.getValue(), params);
        RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
    }
}
