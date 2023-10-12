package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.core.http.handler.ThrowableHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@RouteExceptionHandler
@ActiveReflection
public class NotFoundHandler extends ExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private NotFoundHandler() {}

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
        log.warn(exception.getMessage(), exception);
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

    @Override
    public ThrowableHandler<Exception, NativeResponse<byte[]>> newInstance() {
        return new NotFoundHandler();
    }
}
