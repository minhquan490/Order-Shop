package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.core.http.handler.ThrowableHandler;

import org.springframework.http.HttpStatus;

@ActiveReflection
@RouteExceptionHandler
public class HttpMethodNotSupportedHandler extends ExceptionHandler {

    private HttpMethodNotSupportedHandler() {}

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

    @Override
    public ThrowableHandler<Exception, NativeResponse<byte[]>> newInstance() {
        return new HttpMethodNotSupportedHandler();
    }
}
