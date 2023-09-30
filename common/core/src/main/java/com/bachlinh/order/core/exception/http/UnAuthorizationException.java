package com.bachlinh.order.core.exception.http;

import com.bachlinh.order.core.exception.HttpException;

public class UnAuthorizationException extends HttpException {
    public UnAuthorizationException(String msg, Throwable cause, String url) {
        super(msg, cause, url);
    }

    public UnAuthorizationException(String msg, String url) {
        super(msg, url);
    }
}
