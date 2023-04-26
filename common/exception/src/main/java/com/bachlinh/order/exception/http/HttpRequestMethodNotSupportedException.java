package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.ApplicationException;

public class HttpRequestMethodNotSupportedException extends ApplicationException {
    public HttpRequestMethodNotSupportedException(String message) {
        super(message);
    }

    public HttpRequestMethodNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
