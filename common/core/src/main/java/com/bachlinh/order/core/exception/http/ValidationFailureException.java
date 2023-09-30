package com.bachlinh.order.core.exception.http;

import com.bachlinh.order.core.exception.HttpException;

public class ValidationFailureException extends HttpException {
    public ValidationFailureException(Object message, String url) {
        super(message, url);
    }

    public ValidationFailureException(String message, String url) {
        super(message, url);
    }

    public ValidationFailureException(String message, Throwable cause, String url) {
        super(message, cause, url);
    }
}
