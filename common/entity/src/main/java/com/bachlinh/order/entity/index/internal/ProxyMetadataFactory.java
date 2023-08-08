package com.bachlinh.order.entity.index.internal;

import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.entity.index.spi.FullTextSearchMetadata;
import com.bachlinh.order.entity.index.spi.MetadataFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.proxy.EntityProxy;
import jakarta.persistence.PersistenceException;

class ProxyMetadataFactory implements MetadataFactory {
    private final EntityProxyFactory entityProxyFactory;

    ProxyMetadataFactory(EntityProxyFactory entityProxyFactory) {
        this.entityProxyFactory = entityProxyFactory;
    }

    @Override
    public FullTextSearchMetadata buildFullTextMetadata(Object actualEntity, FieldDescriptor descriptor) {
        var entityType = actualEntity.getClass();
        if (!BaseEntity.class.isAssignableFrom(entityType)) {
            throw new PersistenceException("Entity must be instance of BaseEntity");
        }
        @SuppressWarnings("unchecked")
        EntityProxy proxy = entityProxyFactory.getProxyObject((Class<? extends BaseEntity<?>>) entityType);
        proxy.setTarget(actualEntity);
        return new ProxyFullTextMetadata(proxy);
    }
}
