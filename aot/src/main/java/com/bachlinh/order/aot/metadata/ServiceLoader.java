package com.bachlinh.order.aot.metadata;

import java.util.Collection;

public interface ServiceLoader {

    Collection<ClassMetadata> loadClass(String basePackage);

    Collection<ConstructorMetadata> loadConstructors(ClassMetadata classMetadata);

    Collection<FieldMetadata> loadFields(ClassMetadata classMetadata);

    Collection<MethodMetadata> loadMethods(ClassMetadata classMetadata);
}

