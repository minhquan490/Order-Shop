package com.bachlinh.order.entity.formula.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.FormulaProcessor;
import com.bachlinh.order.entity.formula.SelectFormulaAdapter;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.core.Ordered;

import java.util.Map;

@ActiveReflection
public class CommonFieldSelectFormula extends SelectFormulaAdapter {
    private static final String PATTERN = "%s.%s AS '%s.%s'";

    @ActiveReflection
    public CommonFieldSelectFormula(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        super(resolver, tableMetadata, tableMetadataHolders);
    }

    @Override
    protected String doProcess(String sql, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        String tableName = targetTable.getTableName();
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
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return new CommonFieldSelectFormula(resolver, targetTable, tables);
    }

}
