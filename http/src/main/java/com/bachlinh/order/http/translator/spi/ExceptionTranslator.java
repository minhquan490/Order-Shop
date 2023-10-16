package com.bachlinh.order.http.translator.spi;

public interface ExceptionTranslator<T> {
    T translateException(Exception exception);

    T translateError(Error error);
}
