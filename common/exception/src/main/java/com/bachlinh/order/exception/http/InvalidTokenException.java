package com.bachlinh.order.exception.http;

import lombok.NonNull;
import com.bachlinh.order.exception.HttpException;

public class InvalidTokenException extends HttpException {

    public InvalidTokenException(Object message, String url) {
        super(message, url);
    }

    public InvalidTokenException(String message, @NonNull String url) {
        super(message, url);
    }

    public InvalidTokenException(String message, Throwable cause, @NonNull String url) {
        super(message, cause, url);
    }

    public InvalidTokenException(String message) {
        this(message, "");
    }
}
