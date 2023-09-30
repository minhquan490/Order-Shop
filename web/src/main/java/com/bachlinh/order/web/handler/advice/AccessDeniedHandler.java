package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.core.exception.http.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@ActiveReflection
@RouteExceptionHandler
public class AccessDeniedHandler extends ExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ActiveReflection
    public AccessDeniedHandler() {
    }

    @Override
    protected int status() {
        return HttpStatus.FORBIDDEN.value();
    }

    @Override
    protected String[] message(Exception exception) {
        return new String[]{exception.getMessage()};
    }

    @Override
    protected void doOnException(Exception exception) {
        if (exception instanceof AccessDeniedException accessDeniedException) {
            logger.warn("Customer has ip [{}] access to [{}]", accessDeniedException.getIp(), accessDeniedException.getUrl());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Exception>[] activeOnTypes() {
        return new Class[]{AccessDeniedException.class};
    }

    @Override
    public boolean isErrorHandler() {
        return false;
    }
}
