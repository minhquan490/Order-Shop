package com.bachlinh.order.entity.transaction.internal;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.transaction.spi.AbstractTransactionManager;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.entity.utils.TransactionUtils;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class DefaultTransactionManager extends AbstractTransactionManager<TransactionStatus> {
    private final PlatformTransactionManager platformTransactionManager;

    public DefaultTransactionManager(DependenciesResolver resolver) {
        super(new DefaultSavePointManager());
        this.platformTransactionManager = resolver.resolveDependencies(PlatformTransactionManager.class);
    }

    @Override
    public boolean hasSavePoint() {
        return getSavePointManager().hasSavePoint();
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
    public TransactionHolder<TransactionStatus> beginTransaction() {
        SpringTransactionHolder transactionHolder = new SpringTransactionHolder(platformTransactionManager);
        assignTransactionStatus(transactionHolder);
        DefaultSavePointManager defaultSavePointManager = (DefaultSavePointManager) getSavePointManager();

        defaultSavePointManager.setThreadLocal(getTransactionHolderThreadLocal());
        return transactionHolder;
    }

    @Override
    public void commit(TransactionHolder<TransactionStatus> holder) {
        DefaultTransactionStatus transaction = (DefaultTransactionStatus) holder.getTransaction();
        if (transaction != null) {
            flushTransaction(holder);
            platformTransactionManager.commit(transaction);
        }
        clear();
    }

    @Override
    public void rollback(TransactionHolder<TransactionStatus> holder) {
        TransactionStatus transaction = holder.getTransaction();
        if (transaction != null) {
            platformTransactionManager.rollback(transaction);
        }
        clear();
    }

    @Override
    protected void rollbackToSavePoint(Object savePoint) {
        getTransactionObject().rollbackToSavepoint(savePoint);
    }

    private void flushTransaction(TransactionHolder<TransactionStatus> holder) {
        EntityManager entityManager = TransactionUtils.extractEntityManager(holder);
        if (entityManager != null) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
