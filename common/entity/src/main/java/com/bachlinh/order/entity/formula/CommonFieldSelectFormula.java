package com.bachlinh.order.entity.formula;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.adapter.SelectFormulaAdapter;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.core.Ordered;

import java.util.Map;

public class CommonFieldSelectFormula extends SelectFormulaAdapter {
    private static final String PATTERN = "%s.%s AS '%s.%s'";

    public CommonFieldSelectFormula(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        super(resolver, tableMetadata, tableMetadataHolders);
    }

    @Override
    protected String doProcess(String sql) {
        String tableName = getTargetTable().getTableName();
        return String.format(PATTERN, tableName, "CREATED_BY", tableName, "CREATED_BY") +
                ", " +
                String.format(PATTERN, tableName, "MODIFIED_BY", tableName, "MODIFIED_BY") +
                ", " +
                String.format(PATTERN, tableName, "CREATED_DATE", tableName, "CREATED_DATE") +
                ", " +
                String.format(PATTERN, tableName, "MODIFIED_DATE", tableName, "MODIFIED_DATE") +
                ", " +
                sql;
    }

    @Override
    public int order() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return new CommonFieldSelectFormula(resolver, targetTable, tables);
    }

}
