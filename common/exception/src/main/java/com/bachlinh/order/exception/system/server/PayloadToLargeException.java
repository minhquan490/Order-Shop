package com.bachlinh.order.exception.system.server;

import com.bachlinh.order.exception.ApplicationException;

public class PayloadToLargeException extends ApplicationException {
    public PayloadToLargeException(String message) {
        super(message);
    }

    public PayloadToLargeException(String message, Throwable cause) {
        super(message, cause);
    }
}
