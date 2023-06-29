package com.bachlinh.order.exception.system.dto;

import com.bachlinh.order.exception.ApplicationException;

public class MappingNotFoundException extends ApplicationException {
    public MappingNotFoundException(String message) {
        super(message);
    }

    public MappingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
