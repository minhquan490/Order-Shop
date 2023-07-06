package com.bachlinh.order.dto.adapter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.proxy.Proxy;
import com.bachlinh.order.exception.system.common.CriticalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class DtoProxyInstanceAdapter {

    public static Map<Class<?>, List<Proxy<?, ?>>> instanceProxies() {
        var result = new HashMap<Class<?>, List<Proxy<?, ?>>>();
        for (var clazz : new ApplicationScanner().findComponents()) {
            if (Proxy.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                var instance = initProxy((Class<Proxy<?, ?>>) clazz);
                result.compute(instance.proxyForType(), (aClass, proxies) -> {
                    if (proxies == null) {
                        proxies = new ArrayList<>();
                    }
                    proxies.add(instance);
                    return proxies;
                });
            }
        }
        return result;
    }

    private static Proxy<?, ?> initProxy(Class<Proxy<?, ?>> proxyClass) {
        try {
            var constructor = proxyClass.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new CriticalException(e.getMessage(), e);
        }
    }
}
