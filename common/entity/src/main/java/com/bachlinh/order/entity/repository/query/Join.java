package com.bachlinh.order.entity.repository.query;

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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Join)) return false;
        final Join other = (Join) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$attribute = this.getAttribute();
        final Object other$attribute = other.getAttribute();
        if (this$attribute == null ? other$attribute != null : !this$attribute.equals(other$attribute)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Join;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $attribute = this.getAttribute();
        result = result * PRIME + ($attribute == null ? 43 : $attribute.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        return result;
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
