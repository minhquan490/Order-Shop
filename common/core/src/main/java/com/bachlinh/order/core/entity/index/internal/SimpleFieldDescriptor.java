package com.bachlinh.order.core.entity.index.internal;

import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.core.entity.index.spi.FieldDescriptor;
import jakarta.persistence.PersistenceException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class SimpleFieldDescriptor implements FieldDescriptor {
    private final Set<String> storeableFields = new HashSet<>();
    private final Class<?> entity;

    SimpleFieldDescriptor(Class<?> entity) {
        if (!entity.isAnnotationPresent(EnableFullTextSearch.class)) {
            throw new PersistenceException("The entity must be annotated by @EnableFullTextSearch");
        }
        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(FullTextField.class)) {
                storeableFields.add(field.getName());
            }
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
