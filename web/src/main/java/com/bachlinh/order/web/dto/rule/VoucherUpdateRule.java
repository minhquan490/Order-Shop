package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.VoucherRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherUpdateForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class VoucherUpdateRule extends AbstractRule<VoucherUpdateForm> {
    private VoucherRepository voucherRepository;

    @ActiveReflection
    public VoucherUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(VoucherUpdateForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        validateCommonCase(dto, validationResult);
        validateTimeCase(dto, validationResult);
        validateExistCase(dto, validationResult);

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

    @Override
    protected void injectDependencies() {
        if (voucherRepository == null) {
            voucherRepository = getResolver().resolveDependencies(VoucherRepository.class);
        }
    }

    @Override
    public Class<VoucherUpdateForm> applyOnType() {
        return VoucherUpdateForm.class;
    }

    private void validateCommonCase(VoucherUpdateForm dto, Map<String, List<String>> validationResult) {
        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity voucher for update", validationResult);
        }
        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of voucher must not be empty", validationResult);
        }
        if (dto.getDiscountPercent() < 0) {
            var key = "discount_percent";
            RuntimeUtils.computeMultiValueMap(key, "Discount percent must be positive", validationResult);
        }
        if (!StringUtils.hasText(dto.getTimeStart())) {
            var key = "time_start";
            RuntimeUtils.computeMultiValueMap(key, "Time voucher begin must not be empty", validationResult);
        }
        if (!StringUtils.hasText(dto.getTimeEnd())) {
            var key = "time_end";
            RuntimeUtils.computeMultiValueMap(key, "Time voucher end must not be empty", validationResult);
        }
        if (!StringUtils.hasText(dto.getContent())) {
            var key = "content";
            RuntimeUtils.computeMultiValueMap(key, "Content of voucher must not be empty", validationResult);
        }
        if (dto.getCost() < 0) {
            var key = "cost";
            RuntimeUtils.computeMultiValueMap(key, "Cost of voucher must be positive", validationResult);
        }
    }

    private void validateTimeCase(VoucherUpdateForm dto, Map<String, List<String>> validationResult) {
        if (!ValidateUtils.isValidDate(dto.getTimeStart())) {
            var key = "time_start";
            RuntimeUtils.computeMultiValueMap(key, "Time start of voucher is invalid", validationResult);
        }
        if (!ValidateUtils.isValidDate(dto.getTimeEnd())) {
            var key = "time_end";
            RuntimeUtils.computeMultiValueMap(key, "Time end of voucher is invalid", validationResult);
        }
    }

    private void validateExistCase(VoucherUpdateForm dto, Map<String, List<String>> validationResult) {
        if (voucherRepository.isVoucherNameExist(dto.getName())) {
            var key = "name";
            RuntimeUtils.computeMultiValueMap(key, "Name of voucher is existed", validationResult);
        }
        if (!voucherRepository.isVoucherIdExist(dto.getId())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Voucher not found", validationResult);
        }
    }
}
