package com.bachlinh.order.core.server.netty.listener;

import com.bachlinh.order.core.container.DependenciesResolver;

public abstract class AbstractHttpFrameListenerFactory<T> implements HttpFrameListenerFactory<T> {
    private final DependenciesResolver resolver;

    protected AbstractHttpFrameListenerFactory(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    protected DependenciesResolver getResolver() {
        return resolver;
    }
}
