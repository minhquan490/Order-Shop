package com.bachlinh.order.entity.transaction.spi;

public interface TransactionHolder<T> {
    T getTransaction();

    void cleanup(Object transaction);
}
