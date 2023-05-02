package com.bachlinh.order.core.excecute;

public class BootWrapper<T> {
    private final T source;

    public BootWrapper(T source) {
        this.source = source;
    }

    public T getSource() {
        return source;
    }
}
