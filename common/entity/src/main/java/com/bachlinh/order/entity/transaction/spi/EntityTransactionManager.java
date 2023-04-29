package com.bachlinh.order.entity.transaction.spi;

public interface EntityTransactionManager {
    EntitySavePointManager getSavePointManager();

    boolean hasSavePoint();

    boolean isCompleted();

    boolean isRollbackOnly();

    boolean isNewTransaction();
}
