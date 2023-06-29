package com.bachlinh.order.web.handler.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ErrorHandler;

@RouteExceptionHandler
@ActiveReflection
public class GlobalErrorHandler extends ErrorHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

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
