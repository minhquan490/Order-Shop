package com.bachlinh.order.entity.repository.query;

import java.util.Objects;

public final class OrderBy {
    private final String column;
    private final Type type;

    OrderBy(String column, Type type) {
        this.column = column;
        this.type = type;
    }

    public static OrderByBuilder builder() {
        return new OrderByBuilder();
    }

    public String getColumn() {
        return this.column;
    }

    public Type getType() {
        return this.type;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OrderBy other)) return false;
        final Object this$column = this.getColumn();
        final Object other$column = other.getColumn();
        if (!Objects.equals(this$column, other$column)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        return Objects.equals(this$type, other$type);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $column = this.getColumn();
        result = result * PRIME + ($column == null ? 43 : $column.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        return result;
    }

    public enum Type {
        ASC,
        DESC
    }

    public static class OrderByBuilder {
        private String column;
        private Type type;

        OrderByBuilder() {
        }

        public OrderByBuilder column(String column) {
            this.column = column;
            return this;
        }

        public OrderByBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public OrderBy build() {
            return new OrderBy(this.column, this.type);
        }

        public String toString() {
            return "OrderBy.OrderByBuilder(column=" + this.column + ", type=" + this.type + ")";
        }
    }
}
