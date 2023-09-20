package com.bachlinh.order.entity.formula.processor;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Map;

public abstract class AbstractFormulaProcessor implements FormulaProcessor {
    private final DependenciesResolver dependenciesResolver;

    protected AbstractFormulaProcessor(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
    }

    @Override
    public final String process(String sql) {
        return doProcess(sql);
    }

    protected abstract String doProcess(String sql);

    protected abstract TableMetadataHolder getTargetTable();

    protected abstract Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> getTables();

    protected DependenciesResolver getDependenciesResolver() {
        return this.dependenciesResolver;
    }
}
