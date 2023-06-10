package com.bachlinh.order.entity.index.internal;

import lombok.extern.slf4j.Slf4j;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.proxy.EntityProxy;
import com.bachlinh.order.exception.system.common.CriticalException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
class DefaultEntityProxyFactory implements EntityProxyFactory {
    private final Map<Class<? extends BaseEntity>, EntityProxy> sharedProxies = new LinkedHashMap<>();

    DefaultEntityProxyFactory(ApplicationScanner scanner) {
        var proxies = scanner.findComponents()
                .stream()
                .filter(EntityProxy.class::isAssignableFrom)
                .toList();
        for (Class<?> proxy : proxies) {
            var entityProxy = createSharedProxy(proxy);
            @SuppressWarnings("unchecked")
            var entityType = (Class<? extends BaseEntity>) entityProxy.getWrappedObjectType();
            sharedProxies.put(entityType, entityProxy);
        }
    }

    @Override
    public EntityProxy getProxyObject(Class<? extends BaseEntity> entityClass) {
        return sharedProxies.get(entityClass);
    }

    private EntityProxy createSharedProxy(Class<?> proxyType) {
        try {
            var constructor = proxyType.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (EntityProxy) constructor.newInstance();
        } catch (Exception e) {
            log.error("Can not create proxy [{}]", proxyType.getName(), e);
            throw new CriticalException("Fail to create proxy", e);
        }
    }
}
