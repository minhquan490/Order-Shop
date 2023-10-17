package com.bachlinh.order.repository.query;

import com.google.common.base.Objects;
import jakarta.persistence.criteria.JoinType;

public class Join {
    private final String attribute;
    private final JoinType type;

    Join(String attribute, JoinType type) {
        this.attribute = attribute;
        this.type = type;
    }

    public static JoinBuilder builder() {
        return new JoinBuilder();
    }

    public String getAttribute() {
        return this.attribute;
    }

    public JoinType getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Join join)) return false;
        return Objects.equal(getAttribute(), join.getAttribute()) && getType() == join.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAttribute(), getType());
    }

    public static class JoinBuilder {
        private String attribute;
        private JoinType type;

        JoinBuilder() {
        }

        public JoinBuilder attribute(String attribute) {
            this.attribute = attribute;
            return this;
        }

        public JoinBuilder type(JoinType type) {
            this.type = type;
            return this;
        }

        public Join build() {
            return new Join(this.attribute, this.type);
        }

        public String toString() {
            return "Join.JoinBuilder(attribute=" + this.attribute + ", type=" + this.type + ")";
        }
    }
}
