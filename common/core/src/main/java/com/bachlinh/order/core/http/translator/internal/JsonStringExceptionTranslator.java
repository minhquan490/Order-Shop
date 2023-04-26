package com.bachlinh.order.core.http.translator.internal;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.spi.AbstractExceptionTranslator;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.order.bachlinh.web.routes.advice")
public class JsonStringExceptionTranslator extends AbstractExceptionTranslator<String> {

    @Override
    public NativeResponse<String> translateException(Exception exception) {
        Class<?> exceptionType = exception.getClass();
        try {
            return getExceptionHandlerMap().get(exceptionType).handle(exception);
        } catch (NullPointerException e) {
            return getExceptionHandlerMap().get(Exception.class).handle(exception);
        }
    }

    @Override
    public NativeResponse<String> translateError(Error error) {
        Class<?> errorType = error.getClass();
        try {
            return getErrorHandlerMap().get(errorType).handle(error);
        } catch (NullPointerException e) {
            return getErrorHandlerMap().get(Error.class).handle(error);
        }
    }
}
