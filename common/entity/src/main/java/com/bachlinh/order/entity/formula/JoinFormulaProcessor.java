package com.bachlinh.order.entity.formula;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;

import java.util.Map;

public interface JoinFormulaProcessor extends FormulaProcessor {
    String processJoin(String sql, TableMetadataHolder targetTableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders);
}
