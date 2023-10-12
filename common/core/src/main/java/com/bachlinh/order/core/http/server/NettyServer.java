package com.bachlinh.order.core.http.server;

public interface NettyServer {
    void start() throws InterruptedException;

    void shutdown();
}
