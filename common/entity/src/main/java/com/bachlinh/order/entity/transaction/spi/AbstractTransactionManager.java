package com.bachlinh.order.entity.transaction.spi;

import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class AbstractTransactionManager implements EntityTransactionManager {

    @Override
    public boolean isCompleted() {
        return TransactionAspectSupport.currentTransactionStatus().isCompleted();
    }

    @Override
    public boolean isNewTransaction() {
        return TransactionAspectSupport.currentTransactionStatus().isNewTransaction();
    }

    @Override
    public boolean isRollbackOnly() {
        return TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
    }

    @Override
    public boolean isActualTransactionActive() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }
}
