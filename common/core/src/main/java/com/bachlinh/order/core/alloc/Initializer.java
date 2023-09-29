package com.bachlinh.order.core.alloc;

public interface Initializer<T> {
    T getObject(Class<?> type, Object... params);
}
