package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlSelect extends NativeQueryHolder, SqlOrderBy<SqlSelect>, SqlLimitOffset<SqlSelect>, WhereOperation<SqlWhere> {

    SqlSelect select(Select select);

    SqlSelect select(Select select, String functionName);

    SqlSelect select(Select select, Class<? extends AbstractEntity<?>> table);

    SqlJoin join(Join join);

    SqlJoin join(Join join, NativeQueryHolder subQuery, String subQueryAlias, String currentColumnRef);
}
