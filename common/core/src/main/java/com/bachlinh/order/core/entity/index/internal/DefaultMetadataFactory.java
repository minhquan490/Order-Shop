package com.bachlinh.order.core.entity.index.internal;

import com.bachlinh.order.core.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.core.entity.index.spi.FullTextSearchMetadata;
import com.bachlinh.order.core.entity.index.spi.MetadataFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

class DefaultMetadataFactory implements MetadataFactory {
    private final MultiValueMap<Class<?>, Method> fullTextGetters = new LinkedMultiValueMap<>(new HashMap<>());

    @Override
    public FullTextSearchMetadata buildFullTextMetadata(Object actualEntity, FieldDescriptor descriptor) {
        Collection<Method> getters = fullTextGetters.get(actualEntity.getClass());
        if (getters == null) {
            obtainGetters(actualEntity.getClass(), descriptor);
            return buildFullTextMetadata(actualEntity, descriptor);
        }
        return new SimpleFullTextMetadata(actualEntity, getters, descriptor);
    }

    private void obtainGetters(Class<?> entity, FieldDescriptor fieldDescriptor) {
        String getterPattern = "get{0}{1}";
        List<Method> methods = fieldDescriptor.storeableFields()
                .stream()
                .map(name -> {
                    try {
                        String methodName = MessageFormat.format(getterPattern, name.substring(0, 1).toUpperCase(), name.substring(1));
                        return entity.getMethod(methodName);
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        fullTextGetters.put(entity, methods);
    }
}
