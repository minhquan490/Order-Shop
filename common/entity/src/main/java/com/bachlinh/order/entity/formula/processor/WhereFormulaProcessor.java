package com.bachlinh.order.entity.formula.processor;

public interface WhereFormulaProcessor extends FormulaProcessor {
    String processWhere(String sql);
}
