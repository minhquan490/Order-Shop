package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface WhereOperation<T> {
    T where(Where where);

    T where(Where where, Class<? extends AbstractEntity<?>> table);
}
