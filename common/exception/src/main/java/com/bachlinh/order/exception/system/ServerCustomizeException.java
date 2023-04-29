package com.bachlinh.order.exception.system;

import com.bachlinh.order.exception.ApplicationException;

public class ServerCustomizeException extends ApplicationException {
    public ServerCustomizeException(String message) {
        super(message);
    }

    public ServerCustomizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
