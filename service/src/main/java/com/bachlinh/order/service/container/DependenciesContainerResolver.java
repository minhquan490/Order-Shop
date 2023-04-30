package com.bachlinh.order.service.container;

public interface DependenciesContainerResolver {
    String getContainerClassName();

    Class<?> getContainerType();

    <T> T resolveContainer();

    DependenciesResolver getDependenciesResolver();

    static DependenciesContainerResolver springResolver(Object container) {
        return SpringDependenciesContainerResolver.getInstance(container);
    }

    static DependenciesContainerResolver googleResolver(Object container) {
        // Not support right now
        return null;
    }
}
