package com.bachlinh.order.core.entity.transaction.spi;

public interface EntityTransactionManager {
    EntitySavePointManager getSavePointManager();

    boolean hasSavePoint();

    boolean isCompleted();

    boolean isRollbackOnly();

    boolean isNewTransaction();
}
