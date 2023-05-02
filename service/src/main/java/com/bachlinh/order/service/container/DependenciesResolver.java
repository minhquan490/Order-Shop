package com.bachlinh.order.service.container;

public interface DependenciesResolver {

    <T> T resolveDependencies(Class<T> type);

    <T> T resolveDependencies(String name, Class<T> requireType);

    Object resolveDependencies(String name);
}
