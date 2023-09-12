package com.bachlinh.order.entity.formula.adapter;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.processor.AbstractFormulaProcessor;
import com.bachlinh.order.entity.formula.processor.WhereFormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Map;

public abstract class WhereFormulaAdapter extends AbstractFormulaProcessor implements WhereFormulaProcessor {
    private TableMetadataHolder tableMetadata;
    private Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders;

    private WhereFormulaAdapter(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    protected WhereFormulaAdapter(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
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
    public final String processWhere(String sql) {
        return doProcess(sql);
    }
}
