package com.bachlinh.order.dto.proxy;

public interface DtoProxyFactory {
    <T, U> T createProxy(U source, Class<T> receiverType);

    static DtoProxyFactory defaultInstance() {
        return new DefaultDtoProxyFactory();
    }
}
