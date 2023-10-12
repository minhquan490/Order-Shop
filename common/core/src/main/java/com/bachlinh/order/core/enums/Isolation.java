package com.bachlinh.order.core.enums;

import java.sql.Connection;

public enum Isolation {
    DEFAULT(-1),
    NOT_SUPPORTED(Connection.TRANSACTION_NONE),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int level;

    Isolation(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
