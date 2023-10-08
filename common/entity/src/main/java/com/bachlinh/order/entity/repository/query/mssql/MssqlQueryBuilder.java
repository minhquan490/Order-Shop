package com.bachlinh.order.entity.repository.query.mssql;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlUpdate;

import java.util.Map;

public class MssqlQueryBuilder implements SqlBuilder {

    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;

    public MssqlQueryBuilder(Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    public SqlSelect from(Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder holder = tableMetadata.get(table);
        return new MssqlSelect(holder, (FormulaMetadata) holder, tableMetadata);
    }

    @Override
    public SqlSelect from(Class<? extends AbstractEntity<?>> table, String alias) {
        TableMetadataHolder holder = tableMetadata.get(table);
        var result = new MssqlSelect(holder, (FormulaMetadata) holder, tableMetadata);
        result.setTableAlias(alias);
        return result;
    }

    @Override
    public SqlUpdate updateQueryFor(Class<? extends AbstractEntity<?>> table) {
        return new MssqlUpdate(tableMetadata.get(table));
    }
}
