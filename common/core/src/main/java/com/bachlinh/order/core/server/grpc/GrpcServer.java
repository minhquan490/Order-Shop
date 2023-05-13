package com.bachlinh.order.core.server.grpc;

import io.grpc.BindableService;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.HandlerRegistry;
import io.grpc.ServerCallExecutorSupplier;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServerStreamTracer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public interface GrpcServer {
    GrpcServer start() throws IOException;

    GrpcServer shutdown();

    GrpcServer shutdownNow();

    boolean isShutdown();

    boolean isTerminated();

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    void awaitTermination() throws InterruptedException;

    int getPort();

    List<? extends SocketAddress> getListenSockets();

    List<ServerServiceDefinition> getServices();

    List<ServerServiceDefinition> getImmutableServices();

    List<ServerServiceDefinition> getMutableServices();

    interface Builder {
        Builder forPort(int port);

        Builder addService(BindableService service);

        Builder addService(ServerServiceDefinition service);

        Builder addServices(List<ServerServiceDefinition> services);

        Builder addStreamTracerFactory(ServerStreamTracer.Factory factory);

        Builder addTransportFilter(ServerTransportFilter filter);

        GrpcServer build();

        Builder callExecutor(ServerCallExecutorSupplier executorSupplier);

        Builder compressorRegistry(CompressorRegistry registry);

        Builder decompressorRegistry(DecompressorRegistry registry);

        Builder executor(Executor executor);

        Builder fallbackHandlerRegistry(HandlerRegistry fallbackRegistry);

        Builder handshakeTimeout(long timeout, TimeUnit unit);

        Builder intercept(ServerInterceptor interceptor);

        Builder keepAliveTime(long keepAliveTime, TimeUnit timeUnit);

        Builder keepAliveTimeout(long keepAliveTimeout, TimeUnit timeUnit);

        Builder maxConnectionIdle(long maxConnectionIdle, TimeUnit timeUnit);

        Builder useTransportSecurity(File certChain, File privateKey);

        Builder useTransportSecurity(InputStream certChain, InputStream privateKey);

        Builder useTransportSecurity(String certChain, String privateKey);
    }
}