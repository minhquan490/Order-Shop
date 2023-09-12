package com.bachlinh.order.entity.formula;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.processor.AbstractFormulaProcessor;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.core.Ordered;

import java.text.MessageFormat;
import java.util.Map;

public class IdFieldFormula extends AbstractFormulaProcessor {
    private final TableMetadataHolder targetTable;

    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables;

    private IdFieldFormula(DependenciesResolver dependenciesResolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        super(dependenciesResolver);
        this.targetTable = targetTable;
        this.tables = tables;
    }

    @Override
    protected String doProcess(String sql) {
        String template = "{0}, {1}";
        String idColumnName = targetTable.getColumn("id");
        if (!sql.contains(idColumnName)) {
            return MessageFormat.format(template, idColumnName, sql);
        }
        return sql;
    }

    @Override
    protected TableMetadataHolder getTargetTable() {
        return targetTable;
    }

    @Override
    protected Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> getTables() {
        return tables;
    }

    @Override
    public int order() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return new IdFieldFormula(resolver, targetTable, tables);
    }
}
