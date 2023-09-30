package com.bachlinh.order.entity.transaction.spi;

import org.springframework.core.NamedThreadLocal;

public abstract class AbstractTransactionManager<T> implements EntityTransactionManager<T> {

    private static final ThreadLocal<TransactionHolder<?>> TRANSACTION_HOLDER_THREAD_LOCAL = new NamedThreadLocal<>("Transaction");

    private final EntitySavePointManager savePointManager;

    protected AbstractTransactionManager(EntitySavePointManager entitySavePointManager) {
        this.savePointManager = entitySavePointManager;
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
}
