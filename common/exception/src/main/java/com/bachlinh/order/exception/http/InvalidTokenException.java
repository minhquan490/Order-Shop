package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.ApplicationException;

public class InvalidTokenException extends ApplicationException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
