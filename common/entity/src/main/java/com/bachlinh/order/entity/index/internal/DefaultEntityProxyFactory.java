package com.bachlinh.order.entity.index.internal;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.proxy.AbstractEntityProxy;
import com.bachlinh.order.entity.proxy.EntityProxy;
import com.bachlinh.order.exception.system.common.CriticalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

class DefaultEntityProxyFactory implements EntityProxyFactory {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<Class<? extends BaseEntity<?>>, EntityProxy> sharedProxies = new LinkedHashMap<>();

    DefaultEntityProxyFactory(ApplicationScanner scanner) {
        var proxies = scanner.findComponents()
                .stream()
                .filter(EntityProxy.class::isAssignableFrom)
                .toList();
        for (Class<?> proxy : proxies) {
            var entityProxy = createSharedProxy(proxy);
            @SuppressWarnings("unchecked")
            var entityType = (Class<? extends BaseEntity<?>>) entityProxy.getWrappedObjectType();
            sharedProxies.put(entityType, entityProxy);
        }
    }

    @Override
    public EntityProxy getProxyObject(Class<? extends BaseEntity<?>> entityClass) {
        try {
            var proxy = (AbstractEntityProxy) sharedProxies.get(entityClass);
            var cloned = proxy.clone();
            return (EntityProxy) cloned;
        } catch (CloneNotSupportedException e) {
            throw new CriticalException("Proxy object must be implement Cloneable", e);
        }
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
