package com.bachlinh.order.core.enums;

public enum RequestMethod {
    GET,
    PUT,
    POST,
    DELETE;

    public boolean match(String method) {
        if (method.equalsIgnoreCase("get")) {
            return true;
        }
        if (method.equalsIgnoreCase("put")) {
            return true;
        }
        if (method.equalsIgnoreCase("post")) {
            return true;
        }
        return method.equalsIgnoreCase("delete");
    }
}
