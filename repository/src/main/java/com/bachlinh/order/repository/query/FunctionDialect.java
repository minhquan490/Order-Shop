package com.bachlinh.order.repository.query;

import com.bachlinh.order.repository.query.mssql.MssqlFunctionDialect;

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
            case "com.microsoft.sqlserver.jdbc.SQLServerDriver" -> getMssqlFunctionDialect();
            default -> null;
        };
    }
}
