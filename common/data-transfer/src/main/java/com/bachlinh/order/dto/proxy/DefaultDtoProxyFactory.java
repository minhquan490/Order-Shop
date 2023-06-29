package com.bachlinh.order.dto.proxy;

import com.bachlinh.order.dto.adapter.DtoProxyInstanceAdapter;
import com.bachlinh.order.exception.system.common.DtoProxyConvertException;

import java.util.HashMap;
import java.util.Map;

class DefaultDtoProxyFactory implements DtoProxyFactory {
    private final Map<Class<?>, Object> dtoProxyMap = new HashMap<>(DtoProxyInstanceAdapter.instanceProxies());

    @Override
    @SuppressWarnings("unchecked")
    public <T, U> T createProxy(U source, Class<T> receiverType) {
        var proxy = dtoProxyMap.get(receiverType);
        if (proxy == null) {
            throw new DtoProxyConvertException("Can not create dto be cause missing proxy object");
        }
        ((Proxy<T, U>) proxy).wrap(source);
        return (T) proxy;
    }

    @Override
    public boolean canCreate(Class<?> type) {
        return dtoProxyMap.containsKey(type);
    }

    @Override
    public synchronized void registerProxy(Class<?> type, Proxy<?, ?> proxy) {
        if (!canCreate(type)) {
            dtoProxyMap.put(type, proxy);
        }
    }
}
