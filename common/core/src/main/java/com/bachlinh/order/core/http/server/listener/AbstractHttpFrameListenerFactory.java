package com.bachlinh.order.core.http.server.listener;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.http.handler.Router;
import com.bachlinh.order.core.http.server.channel.security.FilterChainAdapter;

public abstract class AbstractHttpFrameListenerFactory<T> extends AbstractFrameListenerFactory<T> {

    private Router<?, ?> router;
    private FilterChainAdapter filterChainAdapter;

    protected AbstractHttpFrameListenerFactory(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected final void inject() {
        if (this.router == null) {
            this.router = (Router<?, ?>) getResolver().resolveDependencies("router");
        }
        if (this.filterChainAdapter == null) {
            this.filterChainAdapter = getResolver().resolveDependencies(FilterChainAdapter.class);
        }
    }

    @SuppressWarnings("unchecked")
    protected Router<Object, Object> getRouter() {
        return (Router<Object, Object>) router;
    }

    protected FilterChainAdapter getFilterChainAdapter() {
        return filterChainAdapter;
    }
}
