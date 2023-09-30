package com.bachlinh.order.core.exception.system.common;

import com.bachlinh.order.core.exception.ApplicationException;

public class TriggerExecutionException extends ApplicationException {
    public TriggerExecutionException(String message) {
        super(message);
    }

    public TriggerExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
