package com.bachlinh.order.core.exception;

public abstract class HttpException extends RuntimeException {
    private final String url;
    private final transient Object message;

    protected HttpException(Object message, String url) {
        super((message instanceof String s) ? s : "");
        this.url = url;
        this.message = message;
    }

    protected HttpException(String message, String url) {
        super(message);
        this.url = url;
        this.message = message;
    }

    protected HttpException(String message, Throwable cause, String url) {
        super(message, cause);
        this.url = url;
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getMessage() {
        if (message instanceof String) {
            return super.getMessage();
        } else {
            return message.toString();
        }
    }
}
