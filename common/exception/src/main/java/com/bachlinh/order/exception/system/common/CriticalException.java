package com.bachlinh.order.exception.system.common;

import com.bachlinh.order.exception.ApplicationException;

public class CriticalException extends ApplicationException {
    public CriticalException(String message) {
        super(message);
    }

    public CriticalException(String message, Throwable cause) {
        super(message, cause);
    }
}
