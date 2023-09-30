package com.bachlinh.order.core.exception.system.batch;

import com.bachlinh.order.core.exception.ApplicationException;

public class JobBuilderException extends ApplicationException {
    public JobBuilderException(String message) {
        super(message);
    }

    public JobBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
