package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface WhereOperation<T> {
    T where(Where where);

    T where(Where where, Class<? extends AbstractEntity<?>> table);
}
