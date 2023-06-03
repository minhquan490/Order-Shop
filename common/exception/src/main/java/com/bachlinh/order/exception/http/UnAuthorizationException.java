package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.HttpException;

public class UnAuthorizationException extends HttpException {
    public UnAuthorizationException(String msg, Throwable cause, String url) {
        super(msg, cause, url);
    }

    public UnAuthorizationException(String msg, String url) {
        super(msg, url);
    }
}
