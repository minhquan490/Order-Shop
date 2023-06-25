package com.bachlinh.order.exception.http;

import lombok.NonNull;
import com.bachlinh.order.exception.HttpException;

public class ValidationFailureException extends HttpException {
    public ValidationFailureException(Object message, String url) {
        super(message, url);
    }

    public ValidationFailureException(String message, @NonNull String url) {
        super(message, url);
    }

    public ValidationFailureException(String message, Throwable cause, @NonNull String url) {
        super(message, cause, url);
    }
}
