package com.bachlinh.order.entity.transaction.spi;

public interface TransactionManager<T> {
    TransactionHolder<T> beginTransaction();

    void commit(TransactionHolder<T> holder);

    void rollback(TransactionHolder<T> holder);

    TransactionHolder<T> getCurrentTransaction();
}
