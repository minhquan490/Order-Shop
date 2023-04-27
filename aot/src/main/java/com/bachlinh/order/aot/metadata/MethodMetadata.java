package com.bachlinh.order.aot.metadata;

import java.lang.reflect.Method;

public interface MethodMetadata extends Metadata {

    Class<?>[] getParameterTypes();

    Class<?> getReturnType();

    Method getTarget();
}

