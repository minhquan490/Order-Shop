package com.bachlinh.order.entity.transaction.internal;

import com.bachlinh.order.core.exception.system.transaction.TransactionUnavailableException;
import com.bachlinh.order.entity.transaction.spi.EntitySavePointManager;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;

class DefaultSavePointManager implements EntitySavePointManager {
    private static final ThreadLocal<Map<String, Object>> savePointContext = ThreadLocal.withInitial(HashMap::new);

    private ThreadLocal<TransactionHolder<?>> threadLocal;

    @Override
    public Object getSavePointAvailable(String name) {
        return savePointContext.get().get(name);
    }

    @Override
    public Object createSavePoint(String name) {
        Map<String, Object> savePointMap = savePointContext.get();
        Object savePoint = getTransaction().createSavepoint();
        savePointMap.put(name, savePoint);
        savePointContext.set(savePointMap);
        return savePoint;
    }

    @Override
    public void rollbackToSavePoint(String name) {
        Map<String, Object> savePointMap = savePointContext.get();
        Object savePoint = savePointMap.get(name);
        if (savePoint == null) {
            return;
        }
        savePointMap.remove(name);
        savePointContext.set(savePointMap);

        TransactionStatus transactionStatus = getTransaction();
        transactionStatus.rollbackToSavepoint(savePoint);
    }

    @Override
    public void release() {
        if (hasSavePoint()) {
            Collection<Object> savePoints = savePointContext.get().values();
            savePointContext.remove();
            TransactionStatus transactionStatus = getTransaction();
            if (transactionStatus.hasSavepoint()) {
                savePoints.forEach(transactionStatus::releaseSavepoint);
            }
        }
        threadLocal.remove();
        threadLocal = null;
    }

    @Override
    public boolean hasSavePoint() {
        return !savePointContext.get().isEmpty();
    }

    public void setThreadLocal(ThreadLocal<TransactionHolder<?>> threadLocal) {
        this.threadLocal = threadLocal;
    }

    @SuppressWarnings("unchecked")
    private TransactionStatus getTransaction() {
        TransactionHolder<TransactionStatus> transactionHolder = (TransactionHolder<TransactionStatus>) threadLocal.get();
        if (transactionHolder == null) {
            throw new TransactionUnavailableException();
        }
        return transactionHolder.getTransaction();
    }
}
