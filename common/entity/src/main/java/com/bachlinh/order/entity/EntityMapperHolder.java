package com.bachlinh.order.entity;

import com.bachlinh.order.entity.model.BaseEntity;

public interface EntityMapperHolder {
    <T extends BaseEntity<?>> EntityMapper<T> getMapper(Class<T> entityType);
}
