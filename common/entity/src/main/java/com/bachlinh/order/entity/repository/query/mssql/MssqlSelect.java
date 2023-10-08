package com.bachlinh.order.entity.repository.query.mssql;

import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.repository.query.AbstractSqlSelection;
import com.bachlinh.order.entity.repository.query.FunctionDialect;

import java.util.Map;

public class MssqlSelect extends AbstractSqlSelection {
    private static final String ALIAS_PATTERN = "%s AS '%s'";

    private final FunctionDialect functionDialect;

    private String tableAlias;

    MssqlSelect(TableMetadataHolder targetMetadata, FormulaMetadata formulaMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata) {
        super(targetMetadata, formulaMetadata, tableMetadata);
        Environment environment = Environment.getInstance(Environment.getMainEnvironmentName());
        this.functionDialect = FunctionDialect.getDialect(environment.getProperty("server.database.driver"));
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    @Override
    protected FunctionDialect getFunctionDialect() {
        return this.functionDialect;
    }

    @Override
    protected String getSelectRecordPattern() {
        return ALIAS_PATTERN;
    }

    @Override
    protected String getTableAlias() {
        return this.tableAlias;
    }
}
