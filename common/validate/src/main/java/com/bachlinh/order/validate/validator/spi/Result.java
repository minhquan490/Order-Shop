package com.bachlinh.order.validate.validator.spi;

import com.bachlinh.order.entity.ValidateResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Result implements ValidateResult {
    private final List<String> errorMessages;

    public Result() {
        this.errorMessages = new ArrayList<>();
    }

    @Override
    public boolean hasError() {
        return !errorMessages.isEmpty();
    }

    @Override
    public Collection<String> getMessages() {
        return errorMessages;
    }

    public void addMessageError(String msg) {
        this.errorMessages.add(msg);
    }
}
