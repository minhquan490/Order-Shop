package com.bachlinh.order.exception.http;

import lombok.NonNull;
import com.bachlinh.order.exception.HttpException;

public class HttpRequestMethodNotSupportedException extends HttpException {

    public HttpRequestMethodNotSupportedException(Object message, String url) {
        super(message, url);
    }

    public HttpRequestMethodNotSupportedException(String message, @NonNull String url) {
        super(message, url);
    }

    public HttpRequestMethodNotSupportedException(String message, Throwable cause, @NonNull String url) {
        super(message, cause, url);
    }

    public HttpRequestMethodNotSupportedException(String message) {
        this(message, "");
    }
}
