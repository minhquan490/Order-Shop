package com.bachlinh.order.entity.transaction.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.bachlinh.order.core.exception.system.transaction.TransactionUnavailableException;

public abstract class AbstractEntitySavepointManager<T> implements EntitySavePointManager {
    private static final ThreadLocal<Map<String, Object>> savePointContext = ThreadLocal.withInitial(HashMap::new);

    private ThreadLocal<TransactionHolder<?>> threadLocal;

    @Override
    public final Object getSavePointAvailable(String name) {
        return savePointContext.get().get(name);
    }

    @Override
    public final Object createSavePoint(String name) {
        Map<String, Object> savePointMap = savePointContext.get();
        Object savePoint = doCreateSavepoint();
        savePointMap.put(name, savePoint);
        savePointContext.set(savePointMap);
        return savePoint;
    }

    @Override
    public final void rollbackToSavePoint(String name) {
        Map<String, Object> savePointMap = savePointContext.get();
        Object savePoint = savePointMap.get(name);
        if (savePoint == null) {
            return;
        }
        savePointMap.remove(name);
        savePointContext.set(savePointMap);
        doRollback(savePoint);
    }

    @Override
    public final void release() {
        if (hasSavePoint()) {
            T transactionStatus = getTransaction();
            doRelease(transactionStatus);
            savePointContext.remove();
        }
        threadLocal.remove();
        threadLocal = null;
    }

    @Override
    public final boolean hasSavePoint() {
        return !savePointContext.get().isEmpty();
    }

    public void setThreadLocal(ThreadLocal<TransactionHolder<?>> threadLocal) {
        this.threadLocal = threadLocal;
    }

    protected abstract Object doCreateSavepoint();

    protected abstract void doRollback(Object savePoint);

    protected abstract void doRelease(T status);

    @SuppressWarnings("unchecked")
    protected T getTransaction() {
        TransactionHolder<T> transactionHolder = (TransactionHolder<T>) threadLocal.get();
        if (transactionHolder == null) {
            throw new TransactionUnavailableException();
        }
        return transactionHolder.getTransaction();
    }

    protected Collection<Object> getAllSavePointsAvailable() {
        return savePointContext.get().values();
    }
}
