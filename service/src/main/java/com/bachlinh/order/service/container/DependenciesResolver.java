package com.bachlinh.order.service.container;

public interface DependenciesResolver {

    <T> T resolveDependencies(Class<T> type);
}
