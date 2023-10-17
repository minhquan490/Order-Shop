package com.bachlinh.order.repository;

import com.bachlinh.order.entity.transaction.spi.TransactionHolder;

public interface RepositoryManager {
    boolean isRepositoryAvailable(Class<?> repositoryInterface);

    <T> T getRepository(Class<T> repositoryInterface);

    void assignTransaction(TransactionHolder<?> transactionHolder);

    void releaseTransaction(TransactionHolder<?> transactionHolder);
}
