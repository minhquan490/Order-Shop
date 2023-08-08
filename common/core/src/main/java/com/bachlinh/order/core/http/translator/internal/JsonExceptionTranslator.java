package com.bachlinh.order.core.http.translator.internal;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.spi.AbstractExceptionTranslator;

public class JsonExceptionTranslator extends AbstractExceptionTranslator<byte[]> {

    @Override
    public NativeResponse<byte[]> translateException(Exception exception) {
        Class<?> exceptionType = exception.getClass();
        try {
            return getExceptionHandlerMap().get(exceptionType).handle(exception);
        } catch (NullPointerException e) {
            return getExceptionHandlerMap().get(Exception.class).handle(exception);
        }
    }

    @Override
    public NativeResponse<byte[]> translateError(Error error) {
        Class<?> errorType = error.getClass();
        try {
            return getErrorHandlerMap().get(errorType).handle(error);
        } catch (NullPointerException e) {
            return getErrorHandlerMap().get(Error.class).handle(error);
        }
    }
}
