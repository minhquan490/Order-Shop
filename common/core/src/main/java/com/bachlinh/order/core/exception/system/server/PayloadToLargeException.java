package com.bachlinh.order.core.exception.system.server;

import com.bachlinh.order.core.exception.ApplicationException;

public class PayloadToLargeException extends ApplicationException {
    public PayloadToLargeException(String message) {
        super(message);
    }
}
