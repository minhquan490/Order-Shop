package com.bachlinh.order.core.http.converter.spi;

public interface Converter<T, U> {
    T convert(U message);
}
