package com.bachlinh.order.entity.transaction.internal;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.transaction.spi.AbstractTransactionManager;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class SpringTransactionManager extends AbstractTransactionManager<TransactionStatus> {
    private final PlatformTransactionManager platformTransactionManager;

    public SpringTransactionManager(DependenciesResolver resolver) {
        super(new SpringSavePointManager());
        this.platformTransactionManager = resolver.resolveDependencies(PlatformTransactionManager.class);
    }

    @Override
    public boolean isCompleted() {
        return getTransactionObject().isCompleted();
    }

    @Override
    public boolean isRollbackOnly() {
        return getTransactionObject().isRollbackOnly();
    }

    @Override
    public boolean isNewTransaction() {
        return getTransactionObject().isNewTransaction();
    }

    @Override
    protected void doCommit(TransactionStatus status) {
        platformTransactionManager.commit(status);
    }

    @Override
    protected void doRollback(TransactionStatus status) {
        platformTransactionManager.rollback(status);
    }

    @Override
    protected void doBegin() {
        SpringSavePointManager springSavePointManager = (SpringSavePointManager) getSavePointManager();

        springSavePointManager.setThreadLocal(getTransactionHolderThreadLocal());
    }

    @Override
    protected void rollbackToSavePoint(Object savePoint) {
        getTransactionObject().rollbackToSavepoint(savePoint);
    }

    @Override
    protected TransactionHolder<TransactionStatus> getActualTransaction() {
        return new SpringTransactionHolder(platformTransactionManager);
    }

}
