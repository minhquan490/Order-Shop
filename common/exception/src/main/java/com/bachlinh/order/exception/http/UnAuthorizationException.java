package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.ApplicationException;

public class UnAuthorizationException extends ApplicationException {
    public UnAuthorizationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UnAuthorizationException(String msg) {
        super(msg);
    }
}
