package com.bachlinh.order.entity.context;

public interface EntityIdProvider {
    int getLastId() throws ClassNotFoundException;
}
