package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlBuilder {
    SqlSelect from(Class<? extends AbstractEntity<?>> table);

    SqlSelect from(Class<? extends AbstractEntity<?>> table, String alias);

    SqlUpdate updateQueryFor(Class<? extends AbstractEntity<?>> table);
}
