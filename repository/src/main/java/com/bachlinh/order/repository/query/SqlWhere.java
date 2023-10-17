package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlWhere extends SqlWhereAppender, NativeQueryHolder, SqlOrderBy<SqlWhere>, SqlLimitOffset<SqlWhere>, WhereOperation<SqlWhere> {

    SqlWhere and(Where where);

    SqlWhere and(Where where, Class<? extends AbstractEntity<?>> table);

    SqlWhere or(Where where);

    SqlWhere or(Where where, Class<? extends AbstractEntity<?>> table);
}
