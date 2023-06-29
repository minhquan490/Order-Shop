package com.bachlinh.order.dto.adapter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bachlinh.order.annotation.DtoProxy;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.proxy.Proxy;
import com.bachlinh.order.exception.system.common.CriticalException;

import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.NONE)
@Slf4j
public final class DtoProxyInstanceAdapter {

    @SuppressWarnings("unchecked")
    public static Map<Class<?>, Proxy<?, ?>> instanceProxies() {
        return new ApplicationScanner().findComponents()
                .stream()
                .filter(Proxy.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(DtoProxy.class))
                .map(clazz -> initProxy((Class<Proxy<?, ?>>) clazz))
                .collect(Collectors.toMap(Proxy::proxyForType, proxy -> proxy));
    }

    private static Proxy<?, ?> initProxy(Class<Proxy<?, ?>> proxyClass) {
        try {
            var constructor = proxyClass.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            log.error("Can not init instance for class [{}]", proxyClass.getName(), e);
            throw new CriticalException(e.getMessage(), e);
        }
    }
}
