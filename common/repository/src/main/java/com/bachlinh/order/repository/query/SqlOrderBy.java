package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlOrderBy<T> {
    T orderBy(OrderBy orderBy);

    T orderBy(OrderBy orderBy, Class<? extends AbstractEntity<?>> table);
}
