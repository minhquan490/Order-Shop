package com.bachlinh.order.core.enums;

public enum RequestMethod {
    GET,
    PATCH,
    POST,
    DELETE,
    OPTION;

    public boolean match(String method) {
        if (method.equalsIgnoreCase("get")) {
            return true;
        }
        if (method.equalsIgnoreCase("patch")) {
            return true;
        }
        if (method.equalsIgnoreCase("post")) {
            return true;
        }
        if (method.equalsIgnoreCase("option")) {
            return true;
        }
        return method.equalsIgnoreCase("delete");
    }

    public static RequestMethod of(String name) {
        String uppercaseName = name.toUpperCase();
        for (var value : values()) {
            if (value.name().equals(uppercaseName)) {
                return value;
            }
        }
        return null;
    }
}
