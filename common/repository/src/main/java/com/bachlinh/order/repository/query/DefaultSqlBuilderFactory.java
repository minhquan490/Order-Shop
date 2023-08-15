package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.model.AbstractEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class DefaultSqlBuilderFactory implements SqlBuilderFactory {
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata = new HashMap<>();

    @SuppressWarnings("unchecked")
    DefaultSqlBuilderFactory(Collection<EntityContext> contexts) {
        for (EntityContext context : contexts) {
            this.tableMetadata.put((Class<? extends AbstractEntity<?>>) context.getEntity().getClass(), context);
        }
    }

    @Override
    public SqlBuilder getQueryBuilder() {
        return new DefaultSqlBuilder(tableMetadata);
    }
}
