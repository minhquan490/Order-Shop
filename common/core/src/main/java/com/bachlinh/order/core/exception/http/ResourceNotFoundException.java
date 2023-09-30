package com.bachlinh.order.core.exception.http;

import com.bachlinh.order.core.exception.HttpException;

public class ResourceNotFoundException extends HttpException {
    public ResourceNotFoundException(String message, String url) {
        super(message, url);
    }

    public ResourceNotFoundException(String message, Throwable cause, String url) {
        super(message, cause, url);
    }
}
