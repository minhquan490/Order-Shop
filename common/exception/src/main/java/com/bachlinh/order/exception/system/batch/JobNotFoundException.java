package com.bachlinh.order.exception.system.batch;

import com.bachlinh.order.exception.ApplicationException;

public class JobNotFoundException extends ApplicationException {
    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
