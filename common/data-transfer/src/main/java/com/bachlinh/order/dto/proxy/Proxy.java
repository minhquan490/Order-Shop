package com.bachlinh.order.dto.proxy;

public interface Proxy<T, U> {
    T wrap(U source);

    Class<T> proxyForType();

    Proxy<?, ?> getInstance();
}
