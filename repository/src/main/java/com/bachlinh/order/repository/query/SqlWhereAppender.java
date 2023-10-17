package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

public interface SqlWhereAppender extends WhereOperation<SqlWhere>, NativeQueryHolder, SqlOrderBy<SqlWhere> {
    SqlWhereAppender appendAnd(Where where);

    SqlWhereAppender appendAnd(Where where, Class<? extends AbstractEntity<?>> table);

    SqlWhereAppender appendOr(Where where);

    SqlWhereAppender appendOr(Where where, Class<? extends AbstractEntity<?>> table);
}
