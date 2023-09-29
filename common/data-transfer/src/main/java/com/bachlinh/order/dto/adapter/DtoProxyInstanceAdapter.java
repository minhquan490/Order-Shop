package com.bachlinh.order.dto.adapter;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.proxy.Proxy;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.utils.UnsafeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DtoProxyInstanceAdapter {

    private static final Initializer<Proxy<?, ?>> INITIALIZER = new DtoProxyInitializer();

    private DtoProxyInstanceAdapter() {
    }

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
        return INITIALIZER.getObject(proxyClass);
    }

    private static class DtoProxyInitializer implements Initializer<Proxy<?, ?>> {

        @Override
        public Proxy<?, ?> getObject(Class<?> type, Object... params) {
            try {
                Proxy<?, ?> dummy = (Proxy<?, ?>) UnsafeUtils.allocateInstance(type);
                return dummy.getInstance();
            } catch (Exception e) {
                throw new CriticalException(e.getMessage(), e);
            }
        }
    }
}
