package com.bachlinh.order.core.exception.http;

import com.bachlinh.order.core.exception.HttpException;

public class TemporaryTokenExpiredException extends HttpException {

    public TemporaryTokenExpiredException(Object message, String url) {
        super(message, url);
    }

    public TemporaryTokenExpiredException(String message, String url) {
        super(message, url);
    }

    public TemporaryTokenExpiredException(String message, Throwable cause, String url) {
        super(message, cause, url);
    }

    public TemporaryTokenExpiredException(String message) {
        this(message, "");
    }
}
