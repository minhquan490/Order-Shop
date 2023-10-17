package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlJoin extends NativeQueryHolder, SqlOrderBy<SqlJoin>, SqlLimitOffset<SqlJoin>, WhereOperation<SqlWhere> {
    SqlJoin join(Join join);

    SqlJoin join(Join join, Class<? extends AbstractEntity<?>> table);

    SqlJoin join(Join join, NativeQueryHolder subQuery, String subQueryAlias, String currentColumnRef);
}
