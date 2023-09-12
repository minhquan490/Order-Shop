package com.bachlinh.order.entity;

import com.bachlinh.order.entity.model.BaseEntity;

public interface EntityMapperFactory {
    <T extends BaseEntity<?>> EntityMapper<T> createMapper(Class<T> entityType);
}
