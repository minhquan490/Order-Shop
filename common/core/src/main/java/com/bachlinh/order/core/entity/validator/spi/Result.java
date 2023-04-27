package com.bachlinh.order.core.entity.validator.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Result implements ValidateResult {
    private final List<String> errorMessages;

    Result() {
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
