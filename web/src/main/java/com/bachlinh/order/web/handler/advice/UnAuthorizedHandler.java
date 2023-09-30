package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.exception.http.UnAuthorizationException;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import org.springframework.http.HttpStatus;

@RouteExceptionHandler
@ActiveReflection
public class UnAuthorizedHandler extends ExceptionHandler {

    @Override
    protected int status() {
        return HttpStatus.UNAUTHORIZED.value();
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
        return new Class[]{UnAuthorizationException.class};
    }

    @Override
    public boolean isErrorHandler() {
        return false;
    }
}
