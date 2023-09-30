package com.bachlinh.order.core.exception.system.common;

import com.bachlinh.order.core.exception.ApplicationException;

public class JsonConvertException extends ApplicationException {
    public JsonConvertException(String message) {
        super(message);
    }

    public JsonConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
