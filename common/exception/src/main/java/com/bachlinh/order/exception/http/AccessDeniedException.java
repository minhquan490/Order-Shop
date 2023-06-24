package com.bachlinh.order.exception.http;

import lombok.NonNull;
import com.bachlinh.order.exception.HttpException;

public class AccessDeniedException extends HttpException {

    public AccessDeniedException(Object message, String url) {
        super(message, url);
    }

    public AccessDeniedException(String message, @NonNull String url) {
        super(message, url);
    }

    public AccessDeniedException(String message, Throwable cause, @NonNull String url) {
        super(message, cause, url);
    }

    public AccessDeniedException(String message) {
        this(message, "");
    }
}
