package com.bachlinh.order.entity.repository.query;

import java.util.Objects;

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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Where other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$operation = this.getOperation();
        final Object other$operation = other.getOperation();
        if (!Objects.equals(this$operation, other$operation)) return false;
        final Object this$attribute = this.getAttribute();
        final Object other$attribute = other.getAttribute();
        if (!Objects.equals(this$attribute, other$attribute)) return false;
        final Object this$value = this.getValue();
        final Object other$value = other.getValue();
        return Objects.equals(this$value, other$value);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Where;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $operation = this.getOperation();
        result = result * PRIME + ($operation == null ? 43 : $operation.hashCode());
        final Object $attribute = this.getAttribute();
        result = result * PRIME + ($attribute == null ? 43 : $attribute.hashCode());
        final Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        return result;
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
