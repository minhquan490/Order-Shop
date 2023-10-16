package com.bachlinh.order.entity.transaction.spi;

import jakarta.persistence.EntityManager;
import org.springframework.core.NamedThreadLocal;

import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.entity.utils.TransactionUtils;

public abstract class AbstractTransactionManager<T> implements EntityTransactionManager<T> {

    private static final ThreadLocal<TransactionHolder<?>> TRANSACTION_HOLDER_THREAD_LOCAL = new NamedThreadLocal<>("Transaction");

    private final EntitySavePointManager savePointManager;

    protected AbstractTransactionManager(EntitySavePointManager entitySavePointManager) {
        this.savePointManager = entitySavePointManager;
    }

    @Override
    public boolean hasSavePoint() {
        return getSavePointManager().hasSavePoint();
    }

    @Override
    public boolean isActualTransactionActive() {
        return getTransactionObject() != null;
    }

    @Override
    public void rollbackToSavePoint(String name) {
        Object savePoint = getSavePointAvailable(name);
        if (savePoint != null) {
            rollbackToSavePoint(savePoint);
        }
    }

    @Override
    public Object getSavePointAvailable(String name) {
        return savePointManager.getSavePointAvailable(name);
    }

    @Override
    public Object createSavePoint(String name) {
        return savePointManager.createSavePoint(name);
    }

    @Override
    public void release() {
        savePointManager.release();
    }

    @Override
    public EntitySavePointManager getSavePointManager() {
        return savePointManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TransactionHolder<T> getCurrentTransaction() {
        return (TransactionHolder<T>) TRANSACTION_HOLDER_THREAD_LOCAL.get();
    }

    @Override
    public TransactionHolder<T> beginTransaction() {
        return beginTransaction(Isolation.DEFAULT, -1);
    }

    @Override
    public final TransactionHolder<T> beginTransaction(Isolation isolation, int timeOut) {
        TransactionHolder<T> transactionHolder = getActualTransaction();
        transactionHolder.getTransaction(isolation, timeOut);
        assignTransactionStatus(transactionHolder);
        doBegin();
        return transactionHolder;
    }

    @Override
    public final void commit(TransactionHolder<T> holder) {
        T transaction = holder.getTransaction();
        if (transaction != null) {
            flushTransaction(holder);
            doCommit(transaction);
        }
        clear();
    }

    @Override
    public final void rollback(TransactionHolder<T> holder) {
        T transaction = holder.getTransaction();
        if (transaction != null) {
            doRollback(transaction);
        }
        clear();
    }

    protected void assignTransactionStatus(TransactionHolder<T> transactionHolder) {
        TRANSACTION_HOLDER_THREAD_LOCAL.set(transactionHolder);
    }

    @SuppressWarnings("unchecked")
    protected T getTransactionObject() {
        return (T) TRANSACTION_HOLDER_THREAD_LOCAL.get();
    }

    protected void clear() {
        if (TRANSACTION_HOLDER_THREAD_LOCAL.get() != null) {
            TRANSACTION_HOLDER_THREAD_LOCAL.remove();
        }
        savePointManager.release();
    }

    protected ThreadLocal<TransactionHolder<?>> getTransactionHolderThreadLocal() {
        return TRANSACTION_HOLDER_THREAD_LOCAL;
    }

    protected abstract void rollbackToSavePoint(Object savePoint);

    protected abstract TransactionHolder<T> getActualTransaction();

    protected abstract void doCommit(T status);

    protected abstract void doRollback(T status);

    protected abstract void doBegin();

    private void flushTransaction(TransactionHolder<T> holder) {
        EntityManager entityManager = TransactionUtils.extractEntityManager(holder);
        if (entityManager != null) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
