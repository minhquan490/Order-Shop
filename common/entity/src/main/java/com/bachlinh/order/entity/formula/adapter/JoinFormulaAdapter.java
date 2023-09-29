package com.bachlinh.order.entity.formula.adapter;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.processor.AbstractFormulaProcessor;
import com.bachlinh.order.entity.formula.processor.JoinFormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.core.container.DependenciesResolver;

import java.util.Map;

public abstract class JoinFormulaAdapter extends AbstractFormulaProcessor implements JoinFormulaProcessor {
    private TableMetadataHolder tableMetadata;
    private Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders;

    private JoinFormulaAdapter(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    protected JoinFormulaAdapter(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
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
    public final String processJoin(String sql) {
        return doProcess(sql);
    }
}
