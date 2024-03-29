package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;
import com.bachlinh.order.web.repository.spi.VoucherRepository;
import com.bachlinh.order.core.utils.RuntimeUtils;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.AbstractRule;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherDeleteForm;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveReflection
@DtoValidationRule
public class VoucherDeleteRule extends AbstractRule<VoucherDeleteForm> {
    private static final String CAN_NOT_IDENTITY_MESSAGE_ID = "MSG-000011";
    private static final String NOT_EXISTED_MESSAGE_ID = "MSG-000017";

    private VoucherRepository voucherRepository;
    private MessageSettingRepository messageSettingRepository;

    private VoucherDeleteRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    public AbstractRule<VoucherDeleteForm> getInstance(Environment environment, DependenciesResolver resolver) {
        return new VoucherDeleteRule(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(VoucherDeleteForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(CAN_NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "voucher", "", "delete");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
        } else {
            if (!voucherRepository.isVoucherIdExist(dto.getId())) {
                var key = "id";
                MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Voucher");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
            }
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
            voucherRepository = resolveRepository(VoucherRepository.class);
        }
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    public Class<VoucherDeleteForm> applyOnType() {
        return VoucherDeleteForm.class;
    }
}
