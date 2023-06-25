package com.bachlinh.order.validate.rule;

import com.bachlinh.order.validate.base.ValidatedDto;

import java.util.HashMap;
import java.util.Map;

public final class DtoValidateResult implements ValidatedDto.ValidateResult {
    private final Map<String, Object> result = new HashMap<>();

    @Override
    public Map<String, Object> getErrorResult() {
        return result;
    }

    @Override
    public boolean shouldHandle() {
        return result.isEmpty();
    }

    public void addError(String fieldName, Object messageValue) {
        this.result.put(fieldName, messageValue);
    }
}
