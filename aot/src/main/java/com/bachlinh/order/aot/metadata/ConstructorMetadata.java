package com.bachlinh.order.aot.metadata;

import java.lang.reflect.Constructor;

public interface ConstructorMetadata extends Metadata {

    Class<?>[] getParameterTypes();

    Constructor<?> getTarget();
}

