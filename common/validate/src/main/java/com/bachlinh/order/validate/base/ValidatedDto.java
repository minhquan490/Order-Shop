package com.bachlinh.order.validate.base;

import java.util.Map;

public interface ValidatedDto {
    //Marked interface

    interface ValidateResult {
        Map<String, Object> getErrorResult();

        boolean shouldHandle();
    }
}
