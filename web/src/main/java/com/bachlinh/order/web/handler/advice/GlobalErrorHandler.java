package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.handler.ErrorHandler;
import com.bachlinh.order.http.handler.ThrowableHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RouteExceptionHandler
@ActiveReflection
public class GlobalErrorHandler extends ErrorHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private GlobalErrorHandler() {}

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

    @Override
    public ThrowableHandler<Error, NativeResponse<byte[]>> newInstance() {
        return new GlobalErrorHandler();
    }
}
