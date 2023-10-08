package com.bachlinh.order.entity.transaction.spi;

import com.bachlinh.order.core.enums.Isolation;

public interface TransactionManager<T> {
    TransactionHolder<T> beginTransaction();

    TransactionHolder<T> beginTransaction(Isolation isolation, int timeOut);

    void commit(TransactionHolder<T> holder);

    void rollback(TransactionHolder<T> holder);

    TransactionHolder<T> getCurrentTransaction();
}
