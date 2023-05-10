package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.ApplicationException;

public class TemporaryTokenExpiredException extends ApplicationException {
    public TemporaryTokenExpiredException(String message) {
        super(message);
    }

    public TemporaryTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
