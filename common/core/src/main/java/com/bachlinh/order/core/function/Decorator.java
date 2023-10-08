package com.bachlinh.order.core.function;

import java.io.IOException;

@FunctionalInterface
public interface Decorator<T> {
    void decorate(T target) throws IOException;
}
