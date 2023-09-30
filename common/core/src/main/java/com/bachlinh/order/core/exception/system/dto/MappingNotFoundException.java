package com.bachlinh.order.core.exception.system.dto;

import com.bachlinh.order.core.exception.ApplicationException;

public class MappingNotFoundException extends ApplicationException {
    public MappingNotFoundException(String message) {
        super(message);
    }

    public MappingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
