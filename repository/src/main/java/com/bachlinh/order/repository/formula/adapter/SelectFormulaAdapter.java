package com.bachlinh.order.repository.formula.adapter;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.repository.formula.processor.AbstractFormulaProcessor;
import com.bachlinh.order.repository.formula.processor.SelectFormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.core.container.DependenciesResolver;

import java.util.Map;

public abstract class SelectFormulaAdapter extends AbstractFormulaProcessor implements SelectFormulaProcessor {
    private TableMetadataHolder tableMetadata;
    private Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders;

    private SelectFormulaAdapter(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    protected SelectFormulaAdapter(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        this(resolver);
        this.tableMetadata = tableMetadata;
        this.tableMetadataHolders = tableMetadataHolders;
    }

    @Override
    protected TableMetadataHolder getTargetTable() {
        return this.tableMetadata;
    }

    @Override
    protected Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> getTables() {
        return this.tableMetadataHolders;
    }

    @Override
    public final String processSelect(String sql) {
        if (shouldApply(sql)) {
            return process(sql);
        } else {
            return sql;
        }
    }

    protected abstract boolean shouldApply(String sql);
}
