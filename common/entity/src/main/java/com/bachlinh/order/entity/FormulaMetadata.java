package com.bachlinh.order.entity;

import com.bachlinh.order.entity.formula.FormulaProcessor;
import com.bachlinh.order.entity.formula.JoinFormulaProcessor;
import com.bachlinh.order.entity.formula.SelectFormulaProcessor;
import com.bachlinh.order.entity.formula.WhereFormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;

import java.util.Collection;
import java.util.Map;

public interface FormulaMetadata {
    Collection<FormulaProcessor> getTableProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<SelectFormulaProcessor> getTableSelectProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<JoinFormulaProcessor> getTableJoinProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<WhereFormulaProcessor> getTableWhereProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);
    
    Collection<SelectFormulaProcessor> getColumnSelectProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<JoinFormulaProcessor> getColumnJoinProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<WhereFormulaProcessor> getColumnWhereProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);
}
