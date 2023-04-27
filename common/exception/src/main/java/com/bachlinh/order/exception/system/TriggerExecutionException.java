package com.bachlinh.order.exception.system;

import com.bachlinh.order.exception.ApplicationException;

public class TriggerExecutionException extends ApplicationException {
    public TriggerExecutionException(String message) {
        super(message);
    }

    public TriggerExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
