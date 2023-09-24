package com.bachlinh.order.core.server.netty;

public interface NettyServer {
    void start() throws InterruptedException;

    void shutdown();
}
