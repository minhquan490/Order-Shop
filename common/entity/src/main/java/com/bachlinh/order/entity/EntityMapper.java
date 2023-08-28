package com.bachlinh.order.entity;

import com.bachlinh.order.entity.model.BaseEntity;
import jakarta.persistence.Tuple;

import java.util.Collection;
import java.util.Queue;

public interface EntityMapper<T extends BaseEntity<?>> {
    T map(Tuple resultSet);

    T map(Queue<MappingObject> resultSet);

    boolean canMap(Collection<MappingObject> testTarget);

    record MappingObject(String columnName, Object value) {
    }
}
