package com.bachlinh.order.dto.proxy;

import com.bachlinh.order.dto.adapter.DtoProxyInstanceAdapter;
import com.bachlinh.order.exception.system.common.DtoProxyConvertException;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultDtoProxyFactory implements DtoProxyFactory {
    private final Map<Class<?>, List<Object>> dtoProxyMap = new HashMap<>();

    DefaultDtoProxyFactory() {
        DtoProxyInstanceAdapter.instanceProxies().forEach((aClass, proxies) -> dtoProxyMap.put(aClass, new ArrayList<>(proxies)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, U> T createProxy(U source, Class<T> receiverType) {
        var proxies = dtoProxyMap.get(receiverType);
        if (proxies == null) {
            throw new DtoProxyConvertException("Can not create dto be cause missing proxy object");
        } else {
            for (var proxy : proxies) {
                var delegateType = ((ParameterizedType) proxy.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
                var delegateClass = (Class<?>) delegateType;
                if (delegateClass.isAssignableFrom(source.getClass())) {
                    ((Proxy<T, U>) proxy).wrap(source);
                    return (T) proxy;
                }
            }
        }
        throw new DtoProxyConvertException("Missing proxy or strategy, so can not convert");
    }

    @Override
    public boolean canCreate(Class<?> type) {
        return dtoProxyMap.containsKey(type);
    }

    @Override
    public synchronized void registerProxy(Class<?> type, Proxy<?, ?> proxy) {
        if (!canCreate(type)) {
            dtoProxyMap.compute(type, (aClass, objects) -> {
                if (objects == null) {
                    objects = new ArrayList<>();
                }
                objects.add(proxy);
                return objects;
            });
        }
    }
}
