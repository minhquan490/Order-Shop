package com.bachlinh.order.core.http.parser.spi;

public interface RequestBodyParser<T> {
    <U> U parseRequest(T request, Class<?> expectedType);
}
