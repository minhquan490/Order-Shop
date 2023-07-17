package com.bachlinh.order.core.enums;

public enum RequestMethod {
    GET,
    PATCH,
    POST,
    DELETE;

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
        return method.equalsIgnoreCase("delete");
    }
}
