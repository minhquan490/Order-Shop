package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
class DefaultSqlBuilder implements SqlBuilder {

    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;

    @Override
    public SqlSelection from(Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder holder = tableMetadata.get(table);
        return new SqlSelectionSqm(holder, (FormulaMetadata) holder, tableMetadata);
    }

    @Override
    public SqlSelection from(Class<? extends AbstractEntity<?>> table, String alias) {
        TableMetadataHolder holder = tableMetadata.get(table);
        var result = new SqlSelectionSqm(holder, (FormulaMetadata) holder, tableMetadata);
        result.setTableAlias(alias);
        return result;
    }
}
