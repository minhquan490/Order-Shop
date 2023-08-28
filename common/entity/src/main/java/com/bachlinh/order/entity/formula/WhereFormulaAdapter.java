package com.bachlinh.order.entity.formula;

import com.bachlinh.order.entity.TableMetadataHolder;
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
    public final String processWhere(String sql, TableMetadataHolder targetTableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        return doProcess(sql, targetTableMetadata, tableMetadataHolders);
    }
}
