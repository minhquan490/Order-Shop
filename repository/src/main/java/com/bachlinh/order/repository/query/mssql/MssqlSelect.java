package com.bachlinh.order.repository.query.mssql;

import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.repository.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.query.AbstractSqlSelection;
import com.bachlinh.order.repository.query.FunctionDialect;

import java.util.Map;

public class MssqlSelect extends AbstractSqlSelection {
    private static final String ALIAS_PATTERN = "%s AS '%s'";

    private final FunctionDialect functionDialect;

    MssqlSelect(TableMetadataHolder targetMetadata, FormulaMetadata formulaMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata) {
        super(targetMetadata, formulaMetadata, tableMetadata);
        Environment environment = Environment.getInstance(Environment.getMainEnvironmentName());
        this.functionDialect = FunctionDialect.getDialect(environment.getProperty("server.database.driver"));
    }

    @Override
    protected FunctionDialect getFunctionDialect() {
        return this.functionDialect;
    }

    @Override
    protected String getSelectRecordPattern() {
        return ALIAS_PATTERN;
    }
}
