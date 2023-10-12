package com.bachlinh.order.dto.internal;

import com.bachlinh.order.core.exception.system.dto.MappingNotFoundException;
import com.bachlinh.order.dto.MappingContext;
import com.bachlinh.order.dto.proxy.DtoProxyFactory;
import com.bachlinh.order.dto.proxy.Proxy;

import java.util.Collection;

class ProxyMappingContext implements MappingContext {
    private static final DtoProxyFactory FACTORY = DtoProxyFactory.defaultInstance();

    @Override
    public <T, U> T map(U source, Class<T> type) throws MappingNotFoundException {
        if (!canMap(type)) {
            String message = STR. "Can not found proxy for type [\{ type.getName() }]" ;
            throw new MappingNotFoundException(message);
        }
        return FACTORY.createProxy(source, type);
    }

    @Override
    public void register(Collection<?> sources) {
        var canRegister = sources.stream().allMatch(Proxy.class::isInstance);
        if (canRegister) {
            sources.forEach(o -> {
                Proxy<?, ?> proxy = (Proxy<?, ?>) o;
                FACTORY.registerProxy(proxy.proxyForType(), proxy);
            });
        }
    }

    @Override
    public boolean canMap(Class<?> type) {
        return FACTORY.canCreate(type);
    }
}
