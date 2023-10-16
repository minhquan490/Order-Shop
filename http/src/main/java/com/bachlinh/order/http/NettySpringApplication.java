package com.bachlinh.order.http;

import com.bachlinh.order.core.SpringBootApplication;
import com.bachlinh.order.core.concurrent.ThreadPoolOptionHolder;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.http.server.HttpNettyServerFactory;
import com.bachlinh.order.http.server.NettyServer;
import com.bachlinh.order.http.server.NettyServerFactory;

public class NettySpringApplication {

    protected NettySpringApplication() {
    }

    public static void run(Class<?> primarySource, String[] args) throws InterruptedException {
        DependenciesResolver resolver = SpringBootApplication.run(primarySource, args);
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
