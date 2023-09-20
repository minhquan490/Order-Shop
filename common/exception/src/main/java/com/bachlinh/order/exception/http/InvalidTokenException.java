package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.HttpException;

public class InvalidTokenException extends HttpException {

    public InvalidTokenException(Object message, String url) {
        super(message, url);
    }

    public InvalidTokenException(String message, String url) {
        super(message, url);
    }

    public InvalidTokenException(String message, Throwable cause, String url) {
        super(message, cause, url);
    }

    public InvalidTokenException(String message) {
        this(message, "");
    }
}
