package com.bachlinh.order.core.entity.transaction.spi;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
}
