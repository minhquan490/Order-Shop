package com.bachlinh.order.web.handler.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.exception.http.ResourceNotFoundException;

@RouteExceptionHandler
@ActiveReflection
@Slf4j
public class NotFoundHandler extends ExceptionHandler {

    @ActiveReflection
    public NotFoundHandler() {
    }

    @Override
    protected int status() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    protected String[] message(Exception exception) {
        if (exception instanceof NullPointerException) {
            return new String[]{"Not found"};
        }
        return new String[]{exception.getMessage()};
    }

    @Override
    protected void doOnException(Exception exception) {
        if (exception instanceof NullPointerException e) {
            log.warn("Null pointer cause !", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Exception>[] activeOnTypes() {
        return new Class[]{
                ResourceNotFoundException.class,
                NullPointerException.class
        };
    }

    @Override
    public boolean isErrorHandler() {
        return false;
    }
}
