package com.bachlinh.order.http.translator.spi;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.annotation.Ignore;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.handler.ErrorHandler;
import com.bachlinh.order.http.handler.ExceptionHandler;
import com.bachlinh.order.http.handler.ThrowableHandler;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.utils.UnsafeUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractExceptionTranslator<T> implements ExceptionTranslator<NativeResponse<T>> {
    private final Map<Class<?>, ExceptionHandler> exceptionHandlerMap = new LinkedHashMap<>();
    private final Map<Class<?>, ErrorHandler> errorHandlerMap = new LinkedHashMap<>();
    private final Initializer<ThrowableHandler<?, ?>> throwableHandlerInitializer = createThrowableHandlerInitializer();

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
                        ThrowableHandler<?, ?> instance = throwableHandlerInitializer.getObject(clazz);
                        if (!instance.isErrorHandler()) {
                            ExceptionHandler exceptionHandler = (ExceptionHandler) instance.newInstance();
                            for (Class<?> type : exceptionHandler.activeOnTypes()) {
                                exceptionHandlerMap.put(type, exceptionHandler);
                            }
                        }
                        if (instance.isErrorHandler()) {
                            ErrorHandler errorHandler = (ErrorHandler) instance.newInstance();
                            for (Class<?> type : errorHandler.activeOnTypes()) {
                                errorHandlerMap.put(type, errorHandler);
                            }
                        }
                    } catch (Exception e) {
                        String message = StringTemplate.STR. "Can not load throwable handler [\{ clazz.getName() }]" ;
                        throw new CriticalException(message, e);
                    }
                });
    }

    protected Map<Class<?>, ErrorHandler> getErrorHandlerMap() {
        return errorHandlerMap;
    }

    protected Map<Class<?>, ExceptionHandler> getExceptionHandlerMap() {
        return exceptionHandlerMap;
    }

    private Initializer<ThrowableHandler<?, ?>> createThrowableHandlerInitializer() {
        return (type, unused) -> {
            try {
                return (ThrowableHandler<?, ?>) UnsafeUtils.allocateInstance(type);
            } catch (InstantiationException e) {
                throw new CriticalException(e);
            }
        };
    }
}
