package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;

import java.util.Map;

class DefaultSqlBuilder implements SqlBuilder {

    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;

    public DefaultSqlBuilder(Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    public SqlSelect from(Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder holder = tableMetadata.get(table);
        return new SqlSelectionSqm(holder, (FormulaMetadata) holder, tableMetadata);
    }

    @Override
    public SqlSelect from(Class<? extends AbstractEntity<?>> table, String alias) {
        TableMetadataHolder holder = tableMetadata.get(table);
        var result = new SqlSelectionSqm(holder, (FormulaMetadata) holder, tableMetadata);
        result.setTableAlias(alias);
        return result;
    }

    @Override
    public SqlUpdate updateQueryFor(Class<? extends AbstractEntity<?>> table) {
        return new SqlUpdateSqm(tableMetadata.get(table));
    }
}
