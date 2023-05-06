package com.bachlinh.order.exception.system.utils;

import com.bachlinh.order.exception.ApplicationException;

public class UnsafeException extends ApplicationException {
    public UnsafeException(String message) {
        super(message);
    }

    public UnsafeException(String message, Throwable cause) {
        super(message, cause);
    }
}
