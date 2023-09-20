package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.HttpException;

public class AccessDeniedException extends HttpException {
    private String ip;

    public AccessDeniedException(Object message, String url) {
        super(message, url);
    }

    public AccessDeniedException(String message, String url) {
        super(message, url);
    }

    public AccessDeniedException(String message, Throwable cause, String url) {
        super(message, cause, url);
    }

    public AccessDeniedException(String message) {
        this(message, "");
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
