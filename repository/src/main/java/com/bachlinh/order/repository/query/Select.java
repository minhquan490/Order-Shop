package com.bachlinh.order.repository.query;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Select select)) return false;
        return Objects.equal(getColumn(), select.getColumn()) && Objects.equal(getAlias(), select.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getColumn(), getAlias());
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
