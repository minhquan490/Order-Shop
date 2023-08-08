package com.bachlinh.order.entity;

import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.proxy.EntityProxy;

public interface EntityProxyFactory {
    EntityProxy getProxyObject(Class<? extends BaseEntity<?>> entityClass);
}
