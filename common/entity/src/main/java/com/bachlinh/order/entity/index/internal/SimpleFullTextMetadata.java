package com.bachlinh.order.entity.index.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bachlinh.order.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.entity.index.spi.FullTextSearchMetadata;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Deprecated(forRemoval = true)
class SimpleFullTextMetadata implements FullTextSearchMetadata {
    private final Logger log = LoggerFactory.getLogger(getClass());

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
