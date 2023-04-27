package com.bachlinh.order.core.entity.index.internal;

import com.bachlinh.order.core.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.core.entity.index.spi.FullTextSearchMetadata;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Log4j2
class SimpleFullTextMetadata implements FullTextSearchMetadata {
    private final Collection<Method> fullTextGetter;
    private final Object entityObject;
    private final FieldDescriptor fieldDescriptor;

    SimpleFullTextMetadata(Object entityObject, Collection<Method> fullTextGetter, FieldDescriptor fieldDescriptor) {
        this.entityObject = entityObject;
        this.fullTextGetter = fullTextGetter;
        this.fieldDescriptor = fieldDescriptor;
    }

    @Override
    public Class<?> getEntityType() {
        return fieldDescriptor.parent();
    }

    @Override
    public Map<String, Object> getStoreableFieldValue() {
        Map<String, Object> results = new HashMap<>();
        fullTextGetter.forEach(method -> {
            String name = getFieldName(method.getName());
            try {
                results.put(name, method.invoke(entityObject));
            } catch (Exception e) {
                log.warn("Call method [{}] of entity [{}] failure", name, fieldDescriptor.parent());
                // Ignore
            }
        });
        return results;
    }

    private String getFieldName(String methodName) {
        String fieldName = methodName.replace("get", "");
        return fieldName.toLowerCase();
    }
}
