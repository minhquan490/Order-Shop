package com.bachlinh.order.http.server;

import com.bachlinh.order.core.container.DependenciesResolver;

public interface NettyServer {
    void start() throws InterruptedException;

    void shutdown();

    DependenciesResolver getResolver();
}
