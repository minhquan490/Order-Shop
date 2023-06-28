package com.bachlinh.order.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.bachlinh.order.dto.proxy.DtoProxyFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DtoConverter {
    private static final DtoProxyFactory FACTORY = DtoProxyFactory.defaultInstance();

    public static <T, U> T convert(U source, Class<T> type) {
        return FACTORY.createProxy(source, type);
    }
}
