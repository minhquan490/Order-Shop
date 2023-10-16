package com.bachlinh.order.core.exception.system.batch;

import com.bachlinh.order.core.exception.ApplicationException;

public class JobExecutionException extends ApplicationException {
    public JobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
