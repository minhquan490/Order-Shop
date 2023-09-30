package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RouteExceptionHandler
@ActiveReflection
public class GlobalErrorHandler extends ErrorHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

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
