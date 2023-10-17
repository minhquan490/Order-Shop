package com.bachlinh.order.repository.formula.processor;

public interface SelectFormulaProcessor extends FormulaProcessor {
    String processSelect(String sql);
}
