package com.bachlinh.order.core.enums;

public enum Role {
    ADMIN,
    SEO,
    MARKETING,
    CUSTOMER;

    public static Role of(String roleName) {
        for (Role r : values()) {
            if (r.name().equals(roleName.toUpperCase())) {
                return r;
            }
        }
        return null;
    }
}