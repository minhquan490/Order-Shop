package com.bachlinh.order.core.server.netty;

import com.bachlinh.order.core.container.DependenciesResolver;

import java.util.concurrent.ThreadFactory;

public interface NettyServerFactory {
    NettyServer getServer();

    interface Builder {
        Builder certPath(String certPath);

        Builder keyPath(String keyPath);

        Builder resolver(DependenciesResolver resolver);

        Builder threadFactory(ThreadFactory threadFactory);

        Builder numberOfThread(int numberOfThread);

        Builder hostName(String hostName);

        Builder port(int port);

        NettyServerFactory build();
    }
}
