package com.bachlinh.order.entity.formula.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.FormulaProcessor;
import com.bachlinh.order.entity.formula.WhereFormulaAdapter;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.core.Ordered;

import java.text.MessageFormat;
import java.util.Map;

@ActiveReflection
public class ProductEnableFormula extends WhereFormulaAdapter {
    private static final String PATTERN = "%s, %s";

    @ActiveReflection
    public ProductEnableFormula(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        super(resolver, tableMetadata, tableMetadataHolders);
    }

    @Override
    protected String doProcess(String sql, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        String enableSelect = "{0} = '1'";
        String column = targetTable.getColumn(Product_.ENABLED);
        String select = MessageFormat.format(enableSelect, column);
        return String.format(PATTERN, sql, select);
    }

    @Override
    public int order() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return new ProductEnableFormula(resolver, targetTable, tables);
    }
}
