package com.bachlinh.order.core.exception.system.server;

import com.bachlinh.order.core.exception.ApplicationException;

public class StompProtocolException extends ApplicationException {

    private final String description;

    public StompProtocolException(String message, String description) {
        super(message);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
