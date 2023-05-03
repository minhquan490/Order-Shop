package com.bachlinh.order.web.handler.advice;

import org.springframework.http.HttpStatus;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;

@ActiveReflection
@RouteExceptionHandler
public class HttpMethodNotSupportedHandler extends ExceptionHandler {

    @ActiveReflection
    public HttpMethodNotSupportedHandler() {
    }

    @Override
    protected int status() {
        return HttpStatus.METHOD_NOT_ALLOWED.value();
    }

    @Override
    protected String[] message(Exception exception) {
        return new String[]{exception.getMessage()};
    }

    @Override
    protected void doOnException(Exception exception) {
        // Do nothing
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Exception>[] activeOnTypes() {
        return new Class[]{
                HttpRequestMethodNotSupportedException.class
        };
    }

    @Override
    public boolean isErrorHandler() {
        return false;
    }
}
