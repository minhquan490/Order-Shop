package com.bachlinh.order.core.exception.http;

import com.bachlinh.order.core.exception.HttpException;

public class BadVariableException extends HttpException {

    public BadVariableException(Object message, String url) {
        super(message, url);
    }

    public BadVariableException(String message, String url) {
        super(message, url);
    }

    public BadVariableException(String message, Throwable cause, String url) {
        super(message, cause, url);
    }

    public BadVariableException(String message) {
        this(message, "");
    }
}
