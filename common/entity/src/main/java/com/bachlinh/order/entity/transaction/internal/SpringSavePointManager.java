package com.bachlinh.order.entity.transaction.internal;

import com.bachlinh.order.entity.transaction.spi.AbstractEntitySavepointManager;

import java.util.Collection;

import org.springframework.transaction.TransactionStatus;

class SpringSavePointManager extends AbstractEntitySavepointManager<TransactionStatus> {

    @Override
    protected Object doCreateSavepoint() {
        return getTransaction().createSavepoint();
    }

    @Override
    protected void doRollback(Object savePoint) {
        TransactionStatus transactionStatus = getTransaction();
        transactionStatus.rollbackToSavepoint(savePoint);
    }

    @Override
    protected void doRelease(TransactionStatus status) {
        Collection<Object> savePoints = getAllSavePointsAvailable();
        TransactionStatus transactionStatus = getTransaction();
        if (transactionStatus.hasSavepoint()) {
            savePoints.forEach(transactionStatus::releaseSavepoint);

        }
    }
}
