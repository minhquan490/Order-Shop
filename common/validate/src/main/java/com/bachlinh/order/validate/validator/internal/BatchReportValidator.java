package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.BatchReport;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@ActiveReflection
@ApplyOn(entity = BatchReport.class)
public class BatchReportValidator extends AbstractValidator<BatchReport> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String LENGTH_INVALID_MESSAGE_ID = "MSG-000002";
    private static final String NON_NULL_MESSAGE_ID = "MSG-000003";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(BatchReport entity) {
        Result result = new Result();

        MessageSetting nonEmptyMessageSetting = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting lengthInvalidMessageSetting = messageSettingRepository.getMessageById(LENGTH_INVALID_MESSAGE_ID);
        MessageSetting nonNullMessageSetting = messageSettingRepository.getMessageById(NON_NULL_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getBatchName())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessageSetting.getValue(), entity.getBatchName()));
        }

        if (entity.getBatchName().length() > 100) {
            result.addMessageError(MessageFormat.format(lengthInvalidMessageSetting.getValue(), "batch name", "100"));
        }

        if (entity.getErrorDetail().length() > 500) {
            result.addMessageError(MessageFormat.format(lengthInvalidMessageSetting.getValue(), "batch error", "500"));
        }

        if (entity.getTimeReport() == null) {
            result.addMessageError(MessageFormat.format(nonNullMessageSetting.getValue(), "time report"));
        }
        return result;
    }
}
