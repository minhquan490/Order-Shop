package com.bachlinh.order.core.entity.transaction;

public interface EntitySavePointManager {
    Object getSavePointAvailable(String name);

    Object createSavePoint(String name);

    void rollbackToSavePoint(String name);

    void release();

    boolean hasSavePoint();
}
