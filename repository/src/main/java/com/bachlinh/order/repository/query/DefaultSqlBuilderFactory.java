package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.query.mssql.MssqlQueryBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class DefaultSqlBuilderFactory implements SqlBuilderFactory {
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata = new HashMap<>();
    private final String driverName;

    @SuppressWarnings("unchecked")
    DefaultSqlBuilderFactory(Collection<EntityContext> contexts, String driverClassName) {
        for (EntityContext context : contexts) {
            this.tableMetadata.put((Class<? extends AbstractEntity<?>>) context.getEntity().getClass(), context);
        }
        this.driverName = parseToDriverName(driverClassName);
    }

    @Override
    public SqlBuilder getQueryBuilder() {
        return switch (driverName) {
            case "SQLServerDriver" -> new MssqlQueryBuilder(tableMetadata);
            default -> throw new IllegalStateException("Unexpected value: " + driverName);
        };
    }

    private String parseToDriverName(String driverClassName) {
        String[] driverClassNamePieces = driverClassName.split("\\.");
        return driverClassNamePieces[driverClassNamePieces.length - 1];
    }
}
