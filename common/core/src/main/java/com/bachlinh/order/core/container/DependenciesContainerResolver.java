package com.bachlinh.order.core.container;

import com.bachlinh.order.core.environment.Environment;

public interface DependenciesContainerResolver {
    String getContainerClassName();

    Class<?> getContainerType();

    <T> T resolveContainer();

    DependenciesResolver getDependenciesResolver();

    static DependenciesContainerResolver springResolver(Object container) {
        return SpringDependenciesContainerResolver.getInstance(container);
    }

    static DependenciesContainerResolver googleResolver(Object container) {
        throw new UnsupportedOperationException("Unsupported right now");
    }

    static DependenciesContainerResolver buildResolver(Object container, String profile) {
        Environment environment = Environment.getInstance(profile);
        String containerName = environment.getProperty("dependencies.container.class.name");
        return switch (containerName) {
            case "org.springframework.context.ApplicationContext" ->
                    DependenciesContainerResolver.springResolver(container);
            case "com.google.inject.Injector" -> DependenciesContainerResolver.googleResolver(container);
            default -> throw new IllegalStateException("Unexpected value: " + containerName);
        };
    }
}
