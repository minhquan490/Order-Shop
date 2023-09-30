package com.bachlinh.order.core.exception.system.batch;

import com.bachlinh.order.core.exception.ApplicationException;

public class JobCenterInitializeException extends ApplicationException {
    public JobCenterInitializeException(String message) {
        super(message);
    }

    public JobCenterInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
