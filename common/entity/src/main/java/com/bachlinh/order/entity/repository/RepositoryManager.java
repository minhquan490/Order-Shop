package com.bachlinh.order.entity.repository;

import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;

public interface RepositoryManager {
    boolean isRepositoryAvailable(Class<?> repositoryInterface);

    <T> T getRepository(Class<T> repositoryInterface);

    void assignTransaction(TransactionHolder<?> transactionHolder);

    void releaseTransaction(TransactionHolder<?> transactionHolder);

    static RepositoryManager getSpringRepositoryManager(DependenciesContainerResolver containerResolver) {
        return new SpringRepositoryManager(containerResolver);
    }
}
