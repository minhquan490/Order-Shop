package com.bachlinh.order.repository.formula.processor;

public interface WhereFormulaProcessor extends FormulaProcessor {
    String processWhere(String sql);
}
