package com.bachlinh.order.entity.transaction.spi;

public interface TransactionHolder<T> {
    T getTransaction();

    boolean isActive();

    void cleanup(TransactionHolder<?> holder);
}
