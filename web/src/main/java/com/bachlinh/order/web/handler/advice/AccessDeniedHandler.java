package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.exception.http.AccessDeniedException;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@ActiveReflection
@RouteExceptionHandler
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class AccessDeniedHandler extends ExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
