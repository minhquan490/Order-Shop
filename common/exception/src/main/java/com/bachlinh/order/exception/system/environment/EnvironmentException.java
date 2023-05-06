package com.bachlinh.order.exception.system.environment;

import com.bachlinh.order.exception.ApplicationException;

public class EnvironmentException extends ApplicationException {
    public EnvironmentException(String message) {
        super(message);
    }

    public EnvironmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
