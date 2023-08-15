package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.context.EntityContext;

import java.util.Collection;

public interface SqlBuilderFactory {
    SqlBuilder getQueryBuilder();

    static SqlBuilderFactory defaultInstance(Collection<EntityContext> contexts) {
        return new DefaultSqlBuilderFactory(contexts);
    }
}
