package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.ApplicationException;

public class BadVariableException extends ApplicationException {
    public BadVariableException(String message) {
        super(message);
    }

    public BadVariableException(String message, Throwable cause) {
        super(message, cause);
    }
}
