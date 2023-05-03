package com.bachlinh.order.web.handler.advice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.exception.system.CriticalException;
import com.bachlinh.order.exception.system.JsonConvertException;

@RouteExceptionHandler
@ActiveReflection
public class InternalServerHandler extends ExceptionHandler {
    private static final Logger log = LogManager.getLogger(InternalServerHandler.class);

    @ActiveReflection
    public InternalServerHandler() {
    }

    @Override
    protected int status() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    protected String[] message(Exception exception) {
        return new String[]{"Has problem when handle your request, please contact to admin."};
    }

    @Override
    protected void doOnException(Exception exception) {
        log.error("Internal server error cause, because [{}]", exception.getClass().getName(), exception);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Exception>[] activeOnTypes() {
        return new Class[]{
                CriticalException.class,
                JsonConvertException.class
        };
    }

    @Override
    public boolean isErrorHandler() {
        return false;
    }
}
