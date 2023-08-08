package com.bachlinh.order.exception.http;

import com.bachlinh.order.exception.HttpException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class AccessDeniedException extends HttpException {
    private String ip;

    public AccessDeniedException(Object message, String url) {
        super(message, url);
    }

    public AccessDeniedException(String message, @NonNull String url) {
        super(message, url);
    }

    public AccessDeniedException(String message, Throwable cause, @NonNull String url) {
        super(message, cause, url);
    }

    public AccessDeniedException(String message) {
        this(message, "");
    }
}
