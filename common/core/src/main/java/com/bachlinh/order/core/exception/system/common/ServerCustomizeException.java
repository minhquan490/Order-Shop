package com.bachlinh.order.core.exception.system.common;

import com.bachlinh.order.core.exception.ApplicationException;

public class ServerCustomizeException extends ApplicationException {
    public ServerCustomizeException(String message) {
        super(message);
    }

    public ServerCustomizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
