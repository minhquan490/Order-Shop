package com.bachlinh.order.web.dto.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.entity.model.MessageSetting;
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

    @ActiveReflection
    public VoucherUpdateRule(Environment environment, DependenciesResolver resolver) {
        super(environment, resolver);
    }

    @Override
    protected ValidatedDto.ValidateResult doValidate(VoucherUpdateForm dto) {
        var validationResult = new HashMap<String, List<String>>();

        MessageSetting notEmptyMessage = messageSettingRepository.getMessageById(NOT_EMPTY_MESSAGE_ID);
        MessageSetting positiveMessage = messageSettingRepository.getMessageById(POSITIVE_MESSAGE_ID);
        MessageSetting invalidMessage = messageSettingRepository.getMessageById(INVALID_MESSAGE_ID);

        if (!StringUtils.hasText(dto.getId())) {
            var key = "id";
            MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_IDENTITY_MESSAGE_ID);
            String errorContent = MessageFormat.format(messageSetting.getValue(), "voucher", "", "update");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
            return createResult(validationResult);
        } else {
            if (!voucherRepository.isVoucherIdExist(dto.getId())) {
                var key = "id";
                MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_FOUND_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Voucher");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
            }
        }

        if (!StringUtils.hasText(dto.getName())) {
            var key = "name";
            String errorContent = MessageFormat.format(notEmptyMessage.getValue(), "Name of voucher");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
        } else {
            if (voucherRepository.isVoucherNameExist(dto.getName())) {
                var key = "name";
                MessageSetting messageSetting = messageSettingRepository.getMessageById(EXISTED_MESSAGE_ID);
                String errorContent = MessageFormat.format(messageSetting.getValue(), "Name of voucher");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
            }
        }

        if (dto.getDiscountPercent() < 0) {
            var key = "discount_percent";
            String errorContent = MessageFormat.format(positiveMessage.getValue(), "Discount percent");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
        }

        if (!StringUtils.hasText(dto.getTimeStart())) {
            var key = "time_start";
            String errorContent = MessageFormat.format(notEmptyMessage.getValue(), "Time voucher begin");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
        } else {
            if (!ValidateUtils.isValidDate(dto.getTimeStart())) {
                var key = "time_start";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Time start of voucher");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
            }
        }

        if (!StringUtils.hasText(dto.getTimeEnd())) {
            var key = "time_end";
            String errorContent = MessageFormat.format(notEmptyMessage.getValue(), "Time voucher end");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
        } else {
            if (!ValidateUtils.isValidDate(dto.getTimeEnd())) {
                var key = "time_end";
                String errorContent = MessageFormat.format(invalidMessage.getValue(), "Time end of voucher");
                RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
            }
        }

        if (!StringUtils.hasText(dto.getContent())) {
            var key = "content";
            String errorContent = MessageFormat.format(notEmptyMessage.getValue(), "Content of voucher");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
        }

        if (dto.getCost() < 0) {
            var key = "cost";
            String errorContent = MessageFormat.format(positiveMessage.getValue(), "Cost of voucher");
            RuntimeUtils.computeMultiValueMap(key, errorContent, validationResult);
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
}
