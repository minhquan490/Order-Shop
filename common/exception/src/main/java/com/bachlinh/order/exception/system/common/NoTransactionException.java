package com.bachlinh.order.exception.system.common;

import com.bachlinh.order.exception.ApplicationException;

public class NoTransactionException extends ApplicationException {
    public NoTransactionException(String message) {
        super(message);
    }

    public NoTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
