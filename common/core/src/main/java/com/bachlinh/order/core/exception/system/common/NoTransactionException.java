package com.bachlinh.order.core.exception.system.common;

import com.bachlinh.order.core.exception.ApplicationException;

public class NoTransactionException extends ApplicationException {
    public NoTransactionException(String message) {
        super(message);
    }

    public NoTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
