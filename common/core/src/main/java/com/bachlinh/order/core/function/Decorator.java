package com.bachlinh.order.core.function;

import java.io.IOException;

public interface Decorator<T> {
    void decorate(T target) throws IOException;
}
