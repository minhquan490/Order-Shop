package com.bachlinh.order.exception.system.batch;

import com.bachlinh.order.exception.ApplicationException;

public class JobExistedException extends ApplicationException {
    public JobExistedException(String message) {
        super(message);
    }

    public JobExistedException(String message, Throwable cause) {
        super(message, cause);
    }
}
