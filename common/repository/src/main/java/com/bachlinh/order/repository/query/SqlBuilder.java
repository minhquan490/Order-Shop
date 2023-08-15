package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlBuilder {
    SqlSelection from(Class<? extends AbstractEntity<?>> table);

    SqlSelection from(Class<? extends AbstractEntity<?>> table, String alias);
}
