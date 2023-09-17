package com.bachlinh.order.entity.repository.query;

import java.util.Collection;

public interface FunctionDialect {
    Collection<String> getAllAvailableFunction();

    String count();

    String min();

    String max();

    String sum();

    static FunctionDialect getMssqlFunctionDialect() {
        return new MssqlFunctionDialect();
    }

    static FunctionDialect getDialect(String driverName) {
        return switch (driverName) {
            case "com.microsoft.sqlserver.jdbc.SQLServerDriver" -> new MssqlFunctionDialect();
            default -> null;
        };
    }
}
