package com.bachlinh.order.exception;

import lombok.NonNull;

public abstract class HttpException extends ApplicationException {
    private final String url;

    protected HttpException(String message, @NonNull String url) {
        super(message);
        this.url = url;
    }

    protected HttpException(String message, Throwable cause, @NonNull String url) {
        super(message, cause);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
