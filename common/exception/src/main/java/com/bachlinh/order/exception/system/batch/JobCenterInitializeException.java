package com.bachlinh.order.exception.system.batch;

import com.bachlinh.order.exception.ApplicationException;

public class JobCenterInitializeException extends ApplicationException {
    public JobCenterInitializeException(String message) {
        super(message);
    }

    public JobCenterInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
