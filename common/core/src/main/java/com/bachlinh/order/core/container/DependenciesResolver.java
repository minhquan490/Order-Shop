package com.bachlinh.order.core.container;

import java.io.Closeable;

public sealed interface DependenciesResolver extends Closeable permits AbstractDependenciesResolver {

    <T> T resolveDependencies(Class<T> type);

    <T> T resolveDependencies(String name, Class<T> requireType);

    Object resolveDependencies(String name);

    boolean isActive();
}
