package com.bachlinh.order.aot.metadata;

import java.lang.annotation.Annotation;

public interface Metadata {

    boolean isAccessible();

    void makeAccessible();

    Class<?> getParent();

    RefectionType getType();

    Modifier getModifier();

    Class<? extends Annotation>[] getAnnotationTypes();

    <T extends Annotation> T getAnnotation(Class<T> annotationType);

    String getPackage();

    String getName();

    boolean needRegisterToRuntimeHints();

    boolean isNative();
}

