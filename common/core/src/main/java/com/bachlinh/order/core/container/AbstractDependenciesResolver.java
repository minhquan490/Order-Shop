package com.bachlinh.order.core.container;

public abstract non-sealed class AbstractDependenciesResolver implements DependenciesResolver {
    private static DependenciesResolver self;

    protected static void selfAssign(DependenciesResolver self) {
        AbstractDependenciesResolver.self = self;
    }

    public static DependenciesResolver getSelf() {
        return self;
    }
}
