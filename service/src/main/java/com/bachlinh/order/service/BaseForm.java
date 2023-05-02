package com.bachlinh.order.service;

class BaseForm<T> implements Form<T> {
    private final T value;

    BaseForm(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
