package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlSelection extends NativeQueryHolder, SqlOrderBy<SqlSelection>, SqlLimitOffset<SqlSelection> {

    SqlSelection select(Select select);

    SqlSelection select(Select select, Class<? extends AbstractEntity<?>> table);

    WhereOperation where(Where where);

    WhereOperation where(Where where, Class<? extends AbstractEntity<?>> table);

    JoinOperation join(Join join);

    JoinOperation join(Join join, NativeQueryHolder subQuery, String currentColumnRef);
}
