package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.HttpException;

public class ResourceNotFoundException extends HttpException {
    public ResourceNotFoundException(String message, String url) {
        super(message, url);
    }

    public ResourceNotFoundException(String message, Throwable cause, String url) {
        super(message, cause, url);
    }
}
