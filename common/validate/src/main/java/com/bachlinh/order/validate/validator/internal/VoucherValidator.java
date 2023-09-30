package com.bachlinh.order.validate.validator.internal;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.validate.validator.spi.AbstractValidator;
import com.bachlinh.order.validate.validator.spi.Result;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;

@ActiveReflection
@ApplyOn(entity = Voucher.class)
public class VoucherValidator extends AbstractValidator<Voucher> {
    private static final String NON_EMPTY_MESSAGE_ID = "MSG-000001";
    private static final String SPECIFIC_INVALID_MESSAGE_ID = "MSG-000014";
    private static final String TIME_START_MESSAGE_ID = "MSG-000023";
    private static final String TIME_END_MESSAGE_ID = "MSG-000024";

    private MessageSettingRepository messageSettingRepository;

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
    }

    @Override
    protected ValidateResult doValidate(Voucher entity) {
        var result = new Result();

        MessageSetting nonEmptyMessage = messageSettingRepository.getMessageById(NON_EMPTY_MESSAGE_ID);
        MessageSetting specificInvalidMessage = messageSettingRepository.getMessageById(SPECIFIC_INVALID_MESSAGE_ID);
        MessageSetting timeStartMessage = messageSettingRepository.getMessageById(TIME_START_MESSAGE_ID);
        MessageSetting timeEndMessage = messageSettingRepository.getMessageById(TIME_END_MESSAGE_ID);

        if (!StringUtils.hasText(entity.getVoucherContent())) {
            result.addMessageError(MessageFormat.format(nonEmptyMessage.getValue(), "Content"));
        }
        if (entity.getTimeStart() == null) {
            result.addMessageError(MessageFormat.format(specificInvalidMessage.getValue(), "Time start"));
        }
        if (entity.getTimeExpired() == null) {
            result.addMessageError(MessageFormat.format(specificInvalidMessage.getValue(), "Time end"));
        }
        if (entity.getTimeStart().compareTo(Timestamp.from(Instant.now())) >= 0) {
            result.addMessageError(timeStartMessage.getValue());
        }
        if (entity.getTimeExpired().compareTo(entity.getTimeStart()) > 0) {
            result.addMessageError(timeEndMessage.getValue());
        }
        return result;
    }
}
