package com.bachlinh.order.core.server.netty.event;

import io.netty.channel.EventLoopGroup;

import java.util.concurrent.Executor;

public interface EventLoopGroupFactory {
    EventLoopGroup createLoopGroup(Executor executor);

    static EventLoopGroupFactory defaultInstance() {
        return new DefaultEventLoopGroupFactory();
    }
}
