package com.bachlinh.order.repository.query;

import com.google.common.base.Objects;

public class Where {
    private final Operation operation;
    private final String attribute;
    private final Object value;

    Where(Operation operation, String attribute, Object value) {
        this.operation = operation;
        this.attribute = attribute;
        this.value = value;
    }

    public static WhereBuilder builder() {
        return new WhereBuilder();
    }

    public Operation getOperation() {
        return this.operation;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Where where)) return false;
        return getOperation() == where.getOperation() && Objects.equal(getAttribute(), where.getAttribute()) && Objects.equal(getValue(), where.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getOperation(), getAttribute(), getValue());
    }

    public static class WhereBuilder {
        private Operation operation;
        private String attribute;
        private Object value;

        WhereBuilder() {
        }

        public WhereBuilder operation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public WhereBuilder attribute(String attribute) {
            this.attribute = attribute;
            return this;
        }

        public WhereBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public Where build() {
            return new Where(this.operation, this.attribute, this.value);
        }

        public String toString() {
            return "Where.WhereBuilder(operation=" + this.operation + ", attribute=" + this.attribute + ", value=" + this.value + ")";
        }
    }
}
