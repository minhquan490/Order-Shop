package com.bachlinh.order.core.http.translator.spi;

import com.bachlinh.order.core.annotation.Ignore;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.handler.ErrorHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.core.http.handler.ThrowableHandler;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.exception.system.common.CriticalException;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractExceptionTranslator<T> implements ExceptionTranslator<NativeResponse<T>> {
    private final Map<Class<?>, ExceptionHandler> exceptionHandlerMap = new LinkedHashMap<>();
    private final Map<Class<?>, ErrorHandler> errorHandlerMap = new LinkedHashMap<>();

    @SuppressWarnings("rawtypes")
    protected AbstractExceptionTranslator() {
        ApplicationScanner applicationScanner = new ApplicationScanner();
        applicationScanner.findComponents()
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(RouteExceptionHandler.class))
                .filter(clazz -> !clazz.isAnnotationPresent(Ignore.class))
                .filter(ThrowableHandler.class::isAssignableFrom)
                .toList()
                .forEach(clazz -> {
                    try {
                        ThrowableHandler instance = (ThrowableHandler) clazz.getConstructor().newInstance();
                        if (!instance.isErrorHandler()) {
                            ExceptionHandler exceptionHandler = (ExceptionHandler) instance;
                            for (Class<?> type : exceptionHandler.activeOnTypes()) {
                                exceptionHandlerMap.put(type, exceptionHandler);
                            }
                        }
                        if (instance.isErrorHandler()) {
                            ErrorHandler errorHandler = (ErrorHandler) instance;
                            for (Class<?> type : errorHandler.activeOnTypes()) {
                                errorHandlerMap.put(type, errorHandler);
                            }
                        }
                    } catch (Exception e) {
                        throw new CriticalException("Can not load throwable handler [" + clazz.getName() + "]", e);
                    }
                });
    }

    protected Map<Class<?>, ErrorHandler> getErrorHandlerMap() {
        return errorHandlerMap;
    }

    protected Map<Class<?>, ExceptionHandler> getExceptionHandlerMap() {
        return exceptionHandlerMap;
    }
}
