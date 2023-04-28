package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.ApplicationException;

public class AccessDeniedException extends ApplicationException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
