package com.bachlinh.order.validator.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validator.spi.AbstractValidator;
import com.bachlinh.order.validator.spi.Result;

import java.sql.Timestamp;
import java.time.Instant;

@ActiveReflection
public class VoucherValidator extends AbstractValidator<Voucher> {

    @ActiveReflection
    public VoucherValidator(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void inject() {
        // Do nothing
    }

    @Override
    protected ValidateResult doValidate(Voucher entity) {
        var result = new Result();

        if (entity.getVoucherContent() == null || entity.getVoucherContent().isEmpty()) {
            result.addMessageError("Content: Must not be empty");
        }
        if (entity.getTimeStart() == null) {
            result.addMessageError("Time start: Must be specify");
            return result;
        }
        if (entity.getTimeExpired() == null) {
            result.addMessageError("Time end: Must be specify");
        }
        if (entity.getTimeStart().compareTo(Timestamp.from(Instant.now())) >= 0) {
            result.addMessageError("Time start: Must be greater or equals current time");
        }
        if (entity.getTimeExpired().compareTo(entity.getTimeStart()) > 0) {
            result.addMessageError("Time end: Must be greater than time to start");
        }
        return result;
    }
}
