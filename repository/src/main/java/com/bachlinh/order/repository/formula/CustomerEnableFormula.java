package com.bachlinh.order.repository.formula;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.formula.adapter.WhereFormulaAdapter;
import com.bachlinh.order.repository.formula.processor.FormulaProcessor;

import java.text.MessageFormat;
import java.util.Map;

import org.springframework.core.Ordered;

public class CustomerEnableFormula extends WhereFormulaAdapter {

    private CustomerEnableFormula(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        super(resolver, tableMetadata, tableMetadataHolders);
    }

    @Override
    protected String doProcess(String sql) {
        String piecePattern = "{0} AND {1}";
        String enableSelect = "{0} = '1'";
        String columnName = getTargetTable().getColumn("enabled");
        String select = MessageFormat.format(enableSelect, columnName);
        return MessageFormat.format(piecePattern, sql, select);
    }

    @Override
    public int order() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return new CustomerEnableFormula(resolver, targetTable, tables);
    }
}
