package com.bachlinh.order.exception.system.validate;

import com.bachlinh.order.exception.ApplicationException;

public class RuleInstanceFailureException extends ApplicationException {
    public RuleInstanceFailureException(String message) {
        super(message);
    }

    public RuleInstanceFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
