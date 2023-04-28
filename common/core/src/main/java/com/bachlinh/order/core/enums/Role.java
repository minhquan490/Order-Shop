package com.bachlinh.order.core.enums;

public enum Role {
    CUSTOMER,
    ADMIN;

    public static Role of(String roleName) {
        for (Role r : Role.values()) {
            if (r.name().equals(roleName.toUpperCase())) {
                return r;
            }
        }
        return null;
    }
}
