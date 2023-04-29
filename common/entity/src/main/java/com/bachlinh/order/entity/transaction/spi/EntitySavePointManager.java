package com.bachlinh.order.entity.transaction.spi;

public interface EntitySavePointManager {
    Object getSavePointAvailable(String name);

    Object createSavePoint(String name);

    void rollbackToSavePoint(String name);

    void release();

    boolean hasSavePoint();
}
