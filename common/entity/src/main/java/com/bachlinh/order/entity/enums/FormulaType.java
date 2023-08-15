package com.bachlinh.order.entity.enums;

public enum FormulaType {
    SELECT("SELECT"), WHERE("WHERE"), JOIN("JOIN");

    private final String value;

    FormulaType(String select) {
        this.value = select;
    }

    public String getValue() {
        return value;
    }
}
