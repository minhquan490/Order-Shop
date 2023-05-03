package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.ApplicationException;

import java.util.ArrayList;
import java.util.Collection;

public class ConstraintViolationException extends ApplicationException {
    private final Collection<String> errors = new ArrayList<>();

    public ConstraintViolationException(String msg) {
        super(msg);
    }

    public ConstraintViolationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ConstraintViolationException(Collection<String> errors) {
        super("Validate failure");
        this.errors.addAll(errors);
    }

    public Collection<String> getErrors() {
        return errors;
    }
}
