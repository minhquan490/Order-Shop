package com.bachlinh.order.core.exception.system.server;

import com.bachlinh.order.core.exception.ApplicationException;

import java.util.Map;

public class RequestInvalidException extends ApplicationException {
    private final transient Map<String, Object> attributes;

    public RequestInvalidException(Map<String, Object> attributes) {
        this("", attributes);
    }

    public RequestInvalidException(String message, Map<String, Object> attributes) {
        super(message);
        this.attributes = attributes;
    }

    public RequestInvalidException(String message, Throwable cause, Map<String, Object> attributes) {
        super(message, cause);
        this.attributes = attributes;
    }

    public Map<String, Object> getAtrributes() {
        return attributes;
    }
}
