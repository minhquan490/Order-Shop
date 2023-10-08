package com.bachlinh.order.entity.transaction.spi;

import com.bachlinh.order.core.enums.Isolation;

public interface TransactionHolder<T> {
    T getTransaction();

    T getTransaction(Isolation isolation);

    T getTransaction(Isolation isolation, int timeOut);

    boolean isActive();

    void cleanup(TransactionHolder<?> holder);
}
