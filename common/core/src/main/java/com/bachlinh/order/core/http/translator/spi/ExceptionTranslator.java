package com.bachlinh.order.core.http.translator.spi;

public interface ExceptionTranslator<T> {
    T translateException(Exception exception);

    T translateError(Error error);
}
