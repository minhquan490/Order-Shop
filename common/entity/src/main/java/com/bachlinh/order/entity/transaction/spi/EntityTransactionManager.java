package com.bachlinh.order.entity.transaction.spi;

public interface EntityTransactionManager<T> extends TransactionManager<T>, EntitySavePointManager {
    EntitySavePointManager getSavePointManager();

    boolean hasSavePoint();

    boolean isCompleted();

    boolean isRollbackOnly();

    boolean isNewTransaction();

    boolean isActualTransactionActive();
}
