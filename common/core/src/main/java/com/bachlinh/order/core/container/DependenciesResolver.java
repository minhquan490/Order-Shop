package com.bachlinh.order.core.container;

public sealed interface DependenciesResolver permits AbstractDependenciesResolver {

    <T> T resolveDependencies(Class<T> type);

    <T> T resolveDependencies(String name, Class<T> requireType);

    Object resolveDependencies(String name);
}
