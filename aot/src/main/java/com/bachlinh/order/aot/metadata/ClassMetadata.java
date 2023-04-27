package com.bachlinh.order.aot.metadata;

import java.lang.reflect.Constructor;

public interface ClassMetadata extends Metadata {

    Class<?> getTarget();

    Class<?> getSuperClass();

    Class<?>[] getInterfaces();

    boolean isInterface();

    boolean isAbstract();

    boolean isSerializable();

    boolean isReachable();

    boolean hasDefaultConstructor();

    Constructor<?> getDefaultConstructor();
}
