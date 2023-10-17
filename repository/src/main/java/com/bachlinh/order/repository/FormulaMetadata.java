package com.bachlinh.order.repository;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.formula.processor.FormulaProcessor;
import com.bachlinh.order.repository.formula.processor.JoinFormulaProcessor;
import com.bachlinh.order.repository.formula.processor.SelectFormulaProcessor;
import com.bachlinh.order.repository.formula.processor.WhereFormulaProcessor;

import java.util.Collection;
import java.util.Map;

public interface FormulaMetadata {
    Collection<FormulaProcessor> getNativeQueryProcessor(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<SelectFormulaProcessor> getTableSelectProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<JoinFormulaProcessor> getTableJoinProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<WhereFormulaProcessor> getTableWhereProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<SelectFormulaProcessor> getColumnSelectProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<JoinFormulaProcessor> getColumnJoinProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);

    Collection<WhereFormulaProcessor> getColumnWhereProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables);
}
