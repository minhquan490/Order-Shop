package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@ActiveReflection
@RouteExceptionHandler
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class HttpMethodNotSupportedHandler extends ExceptionHandler {

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
