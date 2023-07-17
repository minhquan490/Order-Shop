package com.bachlinh.order.core.server.netty.event;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;

import java.util.concurrent.Executor;

class DefaultEventLoopGroupFactory implements EventLoopGroupFactory {
    @Override
    public EventLoopGroup createLoopGroup(Executor executor) {
        return new EpollEventLoopGroup(10, executor);
    }
}
