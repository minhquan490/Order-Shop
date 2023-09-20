package com.bachlinh.order.entity.repository.query;

import java.util.Objects;

public final class Select {
    private final String column;
    private final String alias;

    Select(String column, String alias) {
        this.column = column;
        this.alias = alias;
    }

    public static SelectBuilder builder() {
        return new SelectBuilder();
    }

    public String getColumn() {
        return this.column;
    }

    public String getAlias() {
        return this.alias;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Select other)) return false;
        final Object this$column = this.getColumn();
        final Object other$column = other.getColumn();
        if (!Objects.equals(this$column, other$column)) return false;
        final Object this$alias = this.getAlias();
        final Object other$alias = other.getAlias();
        return Objects.equals(this$alias, other$alias);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $column = this.getColumn();
        result = result * PRIME + ($column == null ? 43 : $column.hashCode());
        final Object $alias = this.getAlias();
        result = result * PRIME + ($alias == null ? 43 : $alias.hashCode());
        return result;
    }

    public static class SelectBuilder {
        private String column;
        private String alias;

        SelectBuilder() {
        }

        public SelectBuilder column(String column) {
            this.column = column;
            return this;
        }

        public SelectBuilder alias(String alias) {
            this.alias = alias;
            return this;
        }

        public Select build() {
            return new Select(this.column, this.alias);
        }

        public String toString() {
            return "Select.SelectBuilder(column=" + this.column + ", alias=" + this.alias + ")";
        }
    }
}
