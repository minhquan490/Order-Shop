package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.exception.http.InvalidTokenException;
import com.bachlinh.order.exception.http.ValidationFailureException;
import org.springframework.http.HttpStatus;

@ActiveReflection
@RouteExceptionHandler
public class BadRequestHandler extends ExceptionHandler {

    @ActiveReflection
    public BadRequestHandler() {
        // Do nothing
    }

    @Override
    protected int status() {
        return HttpStatus.BAD_REQUEST.value();
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
                BadVariableException.class,
                InvalidTokenException.class,
                ValidationFailureException.class
        };
    }

    @Override
    public boolean isErrorHandler() {
        return false;
    }
}
