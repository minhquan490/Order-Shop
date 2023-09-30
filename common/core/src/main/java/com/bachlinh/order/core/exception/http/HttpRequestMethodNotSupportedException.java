package com.bachlinh.order.core.exception.http;

import com.bachlinh.order.core.exception.HttpException;

public class HttpRequestMethodNotSupportedException extends HttpException {

    public HttpRequestMethodNotSupportedException(Object message, String url) {
        super(message, url);
    }

    public HttpRequestMethodNotSupportedException(String message, String url) {
        super(message, url);
    }

    public HttpRequestMethodNotSupportedException(String message, Throwable cause, String url) {
        super(message, cause, url);
    }

    public HttpRequestMethodNotSupportedException(String message) {
        this(message, "");
    }
}
