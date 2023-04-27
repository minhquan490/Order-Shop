package com.bachlinh.order.core.entity.index.internal;

import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.core.entity.index.spi.FieldDescriptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class SimpleFieldDescriptor implements FieldDescriptor {
    private final Set<String> storeableFields = new HashSet<>();
    private final Class<?> entity;

    SimpleFieldDescriptor(Class<?> entity) {
        EnableFullTextSearch enableFullTextSearch = entity.getAnnotation(EnableFullTextSearch.class);
        for (FullTextField field : enableFullTextSearch.fields()) {
            storeableFields.add(field.value());
        }
        this.entity = entity;
    }

    @Override
    public Collection<String> storeableFields() {
        return storeableFields;
    }

    @Override
    public Class<?> parent() {
        return entity;
    }
}
