package com.bachlinh.order.entity.proxy;

import java.util.Map;

public interface EntityProxy extends Cloneable {
    Map<String, Object> getStoreableFieldValue();

    void setTarget(Object target);

    Class<?> getWrappedObjectType();
}
