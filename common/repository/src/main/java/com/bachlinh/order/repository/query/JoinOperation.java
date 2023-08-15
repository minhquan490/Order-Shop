package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface JoinOperation extends NativeQueryHolder, SqlOrderBy<JoinOperation>, SqlLimitOffset<JoinOperation> {
    JoinOperation join(Join join);

    JoinOperation join(Join join, NativeQueryHolder subQuery, String currentColumnRef);

    WhereOperation where(Where where);

    WhereOperation where(Where where, Class<? extends AbstractEntity<?>> table);
}
