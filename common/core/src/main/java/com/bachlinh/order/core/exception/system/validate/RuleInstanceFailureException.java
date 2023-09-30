package com.bachlinh.order.core.exception.system.validate;

import com.bachlinh.order.core.exception.ApplicationException;

public class RuleInstanceFailureException extends ApplicationException {
    public RuleInstanceFailureException(String message) {
        super(message);
    }

    public RuleInstanceFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
