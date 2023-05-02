package com.bachlinh.order.service;

public interface Form<T> {
    T get();

    static <U> Form<U> wrap(U value) {
        return new BaseForm<>(value);
    }
}
