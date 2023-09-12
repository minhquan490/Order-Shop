package com.bachlinh.order.entity.formula;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.adapter.WhereFormulaAdapter;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.core.Ordered;

import java.text.MessageFormat;
import java.util.Map;

public class CustomerEnableFormula extends WhereFormulaAdapter {

    private CustomerEnableFormula(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        super(resolver, tableMetadata, tableMetadataHolders);
    }

    @Override
    protected String doProcess(String sql) {
        String piecePattern = "{0} AND {1}";
        String enableSelect = "{0} = '1'";
        String columnName = getTargetTable().getColumn(Customer_.ENABLED);
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
