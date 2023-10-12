package com.bachlinh.order.entity.index.internal;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.utils.UnsafeUtils;
import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.proxy.AbstractEntityProxy;
import com.bachlinh.order.entity.proxy.EntityProxy;
import com.bachlinh.order.core.exception.system.common.CriticalException;

import java.util.LinkedHashMap;
import java.util.Map;

class DefaultEntityProxyFactory implements EntityProxyFactory {

    private final Map<Class<? extends BaseEntity<?>>, EntityProxy> sharedProxies = new LinkedHashMap<>();
    private final Initializer<EntityProxy> entityProxyInitializer = createInitializer();

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
        return entityProxyInitializer.getObject(proxyType);
    }

    private Initializer<EntityProxy> createInitializer() {
        return (type, params) -> {
            try {
                return (EntityProxy) UnsafeUtils.allocateInstance(type);
            } catch (InstantiationException e) {
                throw new CriticalException(e);
            }
        };
    }
}
