package com.bachlinh.order.repository.adapter;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

abstract class FluentQuerySupport<S, R> {

    protected final Class<R> resultType;
    protected final Sort sort;
    protected final Set<String> properties;
    protected final Class<S> entityType;

    private final SpelAwareProxyProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

    FluentQuerySupport(Class<R> resultType, Sort sort, @Nullable Collection<String> properties, Class<S> entityType) {

        this.resultType = resultType;
        this.sort = sort;

        if (properties != null) {
            this.properties = new HashSet<>(properties);
        } else {
            this.properties = Collections.emptySet();
        }

        this.entityType = entityType;
    }

    final Collection<String> mergeProperties(Collection<String> additionalProperties) {

        Set<String> newProperties = new HashSet<>();
        newProperties.addAll(properties);
        newProperties.addAll(additionalProperties);
        return Collections.unmodifiableCollection(newProperties);
    }

    @SuppressWarnings("unchecked")
    final Function<Object, R> getConversionFunction(Class<S> inputType, Class<R> targetType) {

        if (targetType.isAssignableFrom(inputType)) {
            return (Function<Object, R>) Function.identity();
        }

        if (targetType.isInterface()) {
            return o -> projectionFactory.createProjection(targetType, o);
        }

        return o -> DefaultConversionService.getSharedInstance().convert(o, targetType);
    }
}
