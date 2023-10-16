package com.bachlinh.order.http.server.listener;

import com.bachlinh.order.core.container.DependenciesResolver;

public abstract class AbstractFrameListenerFactory<T> implements HttpFrameListenerFactory<T>, StompFrameListenerFactory {
    private final DependenciesResolver resolver;

    protected AbstractFrameListenerFactory(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    protected DependenciesResolver getResolver() {
        return resolver;
    }

    protected abstract HttpFrameListener<T> doCreateHttpFrameListener();

    protected abstract StompFrameListener doCreateStompFrameListener();

    protected abstract void inject();

    @Override
    public final StompFrameListener createStompFrameListener() {
        inject();
        return doCreateStompFrameListener();
    }

    @Override
    public final HttpFrameListener<T> createFrameListener() {
        inject();
        return doCreateHttpFrameListener();
    }
}
