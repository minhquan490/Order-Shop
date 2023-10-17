package com.bachlinh.order.repository.query;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderBy orderBy)) return false;
        return Objects.equal(getColumn(), orderBy.getColumn()) && getType() == orderBy.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getColumn(), getType());
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
