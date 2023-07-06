package com.bachlinh.order.web.dto.rule;

import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.VoucherRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherDeleteForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class VoucherDeleteRule extends AbstractRule<VoucherDeleteForm> {
    private VoucherRepository voucherRepository;

    @ActiveReflection
    public VoucherDeleteRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(VoucherDeleteForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Can not identity voucher for delete", validationResult);
        }

        if (!voucherRepository.isVoucherIdExist(dto.getId())) {
            var key = "id";
            RuntimeUtils.computeMultiValueMap(key, "Voucher is not existed", validationResult);
        }
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
    public Class<VoucherDeleteForm> applyOnType() {
        return VoucherDeleteForm.class;
    }
}
