package com.bachlinh.order.entity.repository.query;

import org.hibernate.tool.schema.spi.SqlScriptException;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;

import java.util.Map;

public abstract class AbstractSqlBuilder implements SqlBuilder {

    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;

    protected AbstractSqlBuilder(Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    public final SqlSelect from(Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder holder = tableMetadata.get(table);
        return doCreateSqlSelect(holder, (FormulaMetadata) holder);
    }

    @Override
    public final SqlSelect from(Class<? extends AbstractEntity<?>> table, String alias) {
        AbstractSqlSelection result = (AbstractSqlSelection) from(table);
        result.setTableAlias(alias);
        return result;
    }

    @Override
    public final SqlUpdate updateQueryFor(Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder holder = getTableMetadata().get(table);
        if (holder != null) {
            return doCreateSqlUpdate(holder);
        }
        throw createUnknownException(table.getName());
    }

    @Override
    public SqlDelete deleteQueryFor(Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder holder = getTableMetadata().get(table);
        if (holder != null) {
            return new SqlDeleteSqm(holder);
        }
        throw createUnknownException(table.getName());
    }

    protected Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> getTableMetadata() {
        return tableMetadata;
    }

    protected abstract SqlSelect doCreateSqlSelect(TableMetadataHolder holder, FormulaMetadata formulaMetadata);

    protected abstract SqlUpdate doCreateSqlUpdate(TableMetadataHolder metadataHolder);

    private SqlScriptException createUnknownException(String tableName) {
        String message = STR. "Unkown table name \{ tableName }" ;
        throw new SqlScriptException(message);
    }
}
