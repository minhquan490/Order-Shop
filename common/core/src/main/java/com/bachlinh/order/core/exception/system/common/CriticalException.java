package com.bachlinh.order.core.exception.system.common;

import com.bachlinh.order.core.exception.ApplicationException;

public class CriticalException extends ApplicationException {
    public CriticalException(String message) {
        super(message);
    }

    public CriticalException(String message, Throwable cause) {
        super(message, cause);
    }

    public CriticalException(Throwable cause) {
        super(cause);
    }
}
