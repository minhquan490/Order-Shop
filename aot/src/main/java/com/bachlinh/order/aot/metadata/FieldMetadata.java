package com.bachlinh.order.aot.metadata;

import java.lang.reflect.Field;

public interface FieldMetadata extends Metadata {

    Class<?> getFieldType();

    Field getTarget();
}

