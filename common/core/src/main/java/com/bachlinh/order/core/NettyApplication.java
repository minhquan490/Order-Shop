package com.bachlinh.order.core;

import com.bachlinh.order.core.concurrent.ThreadPoolOptionHolder;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.http.server.HttpNettyServerFactory;
import com.bachlinh.order.core.http.server.NettyServer;
import com.bachlinh.order.core.http.server.NettyServerFactory;

public class NettyApplication {

    protected NettyApplication() {
    }

    public static void run(Class<?> primarySource, String[] args) throws InterruptedException {
        DependenciesResolver resolver = Application.run(primarySource, args, false);
        NettyServerFactory serverFactory = getNettyServerFactory(resolver);
        NettyServer server = serverFactory.getServer();
        try {
            server.start();
        } finally {
            server.shutdown();
        }
    }

    private static NettyServerFactory getNettyServerFactory(DependenciesResolver resolver) {
        ThreadPoolOptionHolder threadPoolOptionHolder = resolver.resolveDependencies(ThreadPoolOptionHolder.class);
        Environment environment = Environment.getInstance(Environment.getMainEnvironmentName());
        return HttpNettyServerFactory.builder()
                .port(Integer.parseInt(environment.getProperty("server.port")))
                .hostName(environment.getProperty("server.address"))
                .resolver(resolver)
                .threadFactory(threadPoolOptionHolder.getThreadOption().getThreadFactory())
                .numberOfThread(15)
                .certPath(environment.getProperty("server.ssl.certificate"))
                .keyPath(environment.getProperty("server.ssl.certificate-private-key"))
                .environment(environment)
                .build();
    }
}
