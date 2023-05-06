package com.bachlinh.order.entity.transaction.internal;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import com.bachlinh.order.entity.transaction.spi.EntitySavePointManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class DefaultSavePointManager implements EntitySavePointManager {
    private static final ThreadLocal<Map<String, Object>> savePointContext = ThreadLocal.withInitial(HashMap::new);

    @Override
    public Object getSavePointAvailable(String name) {
        return savePointContext.get().get(name);
    }

    @Override
    public Object createSavePoint(String name) {
        Map<String, Object> savePointMap = savePointContext.get();
        Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
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
        TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
    }

    @Override
    public void release() {
        Collection<Object> savePoints = savePointContext.get().values();
        savePointContext.remove();
        TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
        savePoints.forEach(transactionStatus::releaseSavepoint);
    }

    @Override
    public boolean hasSavePoint() {
        return savePointContext.get().isEmpty();
    }
}
