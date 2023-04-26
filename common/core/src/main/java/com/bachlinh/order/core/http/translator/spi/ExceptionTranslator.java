package com.bachlinh.order.core.http.translator.spi;

import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.internal.JsonStringExceptionTranslator;

public interface ExceptionTranslator<T> {
    T translateException(Exception exception);

    T translateError(Error error);

    static ExceptionTranslator<NativeResponse<String>> jsonStringExceptionTranslator() {
        return new JsonStringExceptionTranslator();
    }
}
