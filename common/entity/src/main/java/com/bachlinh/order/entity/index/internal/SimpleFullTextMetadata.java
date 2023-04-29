package com.bachlinh.order.entity.index.internal;

import com.bachlinh.order.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.entity.index.spi.FullTextSearchMetadata;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class SimpleFullTextMetadata implements FullTextSearchMetadata {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(SimpleFullTextMetadata.class);
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
