package com.bachlinh.order.http.converter.spi;

public interface Converter<T, U> {
    T convert(U message);
}
