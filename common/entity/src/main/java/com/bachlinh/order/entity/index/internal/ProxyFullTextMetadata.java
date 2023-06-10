package com.bachlinh.order.entity.index.internal;

import com.bachlinh.order.entity.index.spi.FullTextSearchMetadata;
import com.bachlinh.order.entity.proxy.EntityProxy;

import java.util.Map;

class ProxyFullTextMetadata implements FullTextSearchMetadata {
    private final EntityProxy proxy;

    ProxyFullTextMetadata(EntityProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Class<?> getEntityType() {
        return proxy.getWrappedObjectType();
    }

    @Override
    public Map<String, Object> getStoreableFieldValue() {
        return proxy.getStoreableFieldValue();
    }
}
