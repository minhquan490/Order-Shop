package com.bachlinh.order.web.handler.advice;

import lombok.extern.slf4j.Slf4j;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ErrorHandler;

@RouteExceptionHandler
@ActiveReflection
@Slf4j
public class GlobalErrorHandler extends ErrorHandler {

    @ActiveReflection
    public GlobalErrorHandler() {
    }

    @Override
    protected void executeOnError(Error error) {
        log.error("Error [{}] was occurred", error.getClass().getName(), error);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Error>[] activeOnTypes() {
        return new Class[]{
                Error.class
        };
    }

    @Override
    public boolean isErrorHandler() {
        return true;
    }
}
