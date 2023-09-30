package com.bachlinh.order.core.exception.system.validate;

import com.bachlinh.order.core.exception.ApplicationException;

public class ValidationRuleNotFoundException extends ApplicationException {
    public ValidationRuleNotFoundException(String message) {
        super(message);
    }

    public ValidationRuleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
