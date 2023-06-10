package com.bachlinh.order.entity.index.internal;

import jakarta.persistence.PersistenceException;
import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.entity.index.spi.FullTextSearchMetadata;
import com.bachlinh.order.entity.index.spi.MetadataFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.proxy.EntityProxy;

class ProxyMetadataFactory implements MetadataFactory {
    private final EntityProxyFactory entityProxyFactory;

    ProxyMetadataFactory(EntityProxyFactory entityProxyFactory) {
        this.entityProxyFactory = entityProxyFactory;
    }

    @Override
    public FullTextSearchMetadata buildFullTextMetadata(Object actualEntity, FieldDescriptor descriptor) {
        @SuppressWarnings("unchecked")
        var entityType = (Class<? extends BaseEntity>) actualEntity.getClass();
        if (!BaseEntity.class.isAssignableFrom(entityType)) {
            throw new PersistenceException("Entity must be instance of BaseEntity");
        }
        EntityProxy proxy = entityProxyFactory.getProxyObject(entityType);
        proxy.setTarget(actualEntity);
        return new ProxyFullTextMetadata(proxy);
    }
}