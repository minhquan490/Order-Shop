package com.bachlinh.order.core.entity.transaction;

public interface EntityTransactionManager {
    EntitySavePointManager getSavePointManager();

    boolean hasSavePoint();

    boolean isCompleted();

    boolean isRollbackOnly();

    boolean isNewTransaction();
}
