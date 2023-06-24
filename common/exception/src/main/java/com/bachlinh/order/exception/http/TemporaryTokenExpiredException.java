package com.bachlinh.order.exception.http;

import lombok.NonNull;
import com.bachlinh.order.exception.HttpException;

public class TemporaryTokenExpiredException extends HttpException {

    public TemporaryTokenExpiredException(Object message, String url) {
        super(message, url);
    }

    public TemporaryTokenExpiredException(String message, @NonNull String url) {
        super(message, url);
    }

    public TemporaryTokenExpiredException(String message, Throwable cause, @NonNull String url) {
        super(message, cause, url);
    }

    public TemporaryTokenExpiredException(String message) {
        this(message, "");
    }
}
