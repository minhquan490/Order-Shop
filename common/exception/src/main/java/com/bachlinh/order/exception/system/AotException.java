package com.bachlinh.order.exception.system;

import com.bachlinh.order.exception.ApplicationException;

public class AotException extends ApplicationException {
    public AotException(String message) {
        super(message);
    }

    public AotException(String message, Throwable cause) {
        super(message, cause);
    }
}
