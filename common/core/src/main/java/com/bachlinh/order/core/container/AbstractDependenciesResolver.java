package com.bachlinh.order.core.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract non-sealed class AbstractDependenciesResolver implements DependenciesResolver {
    private static DependenciesResolver self;

    private final Map<Class<?>, Object> queriedBeanClasses = new ConcurrentHashMap<>();
    private final Map<String, Object> queriedBeanNames = new ConcurrentHashMap<>();

    protected static void selfAssign(DependenciesResolver self) {
        AbstractDependenciesResolver.self = self;
    }

    public static DependenciesResolver getSelf() {
        return self;
    }

    protected void cacheBean(@NonNull Class<?> type, @NonNull Object bean) {
        this.queriedBeanClasses.putIfAbsent(type, bean);
    }

    protected void cacheBean(@NonNull String beanName, @NonNull Object bean) {
        this.queriedBeanNames.putIfAbsent(beanName, bean);
    }

    @Nullable
    protected Object queryBean(Class<?> beanType) {
        return this.queriedBeanClasses.get(beanType);
    }

    @Nullable
    protected Object queryBean(String beanName) {
        return this.queriedBeanNames.get(beanName);
    }
}
