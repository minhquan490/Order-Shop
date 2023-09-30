package com.bachlinh.order.core.exception.system.batch;

import com.bachlinh.order.core.exception.ApplicationException;

public class JobExistedException extends ApplicationException {
    public JobExistedException(String message) {
        super(message);
    }

    public JobExistedException(String message, Throwable cause) {
        super(message, cause);
    }
}
