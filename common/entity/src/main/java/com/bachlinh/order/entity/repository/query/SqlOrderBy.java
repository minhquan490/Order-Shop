package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlOrderBy<T> {
    T orderBy(OrderBy orderBy);

    T orderBy(OrderBy orderBy, Class<? extends AbstractEntity<?>> table);
}
