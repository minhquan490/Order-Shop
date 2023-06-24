package com.bachlinh.order.exception.http;

import lombok.NonNull;
import com.bachlinh.order.exception.HttpException;

public class BadVariableException extends HttpException {

    public BadVariableException(Object message, String url) {
        super(message, url);
    }

    public BadVariableException(String message, @NonNull String url) {
        super(message, url);
    }

    public BadVariableException(String message, Throwable cause, @NonNull String url) {
        super(message, cause, url);
    }

    public BadVariableException(String message) {
        this(message, "");
    }
}
