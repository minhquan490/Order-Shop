package com.bachlinh.order.entity.formula;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Map;

public interface FormulaProcessor {
    String process(String sql);

    int order();

    FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);
}
