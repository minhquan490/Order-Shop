package com.bachlinh.order.core.server.grpc.internal;

import io.grpc.BindableService;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.HandlerRegistry;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCallExecutorSupplier;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServerStreamTracer;
import com.bachlinh.order.core.server.grpc.GrpcServer;
import com.bachlinh.order.core.server.grpc.ServerInterceptor;
import com.bachlinh.order.core.server.grpc.ServerTransportFilter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class DefaultGrpcServer implements GrpcServer {
    private final Server internalServer;

    private DefaultGrpcServer(Server internalServer) {
        this.internalServer = internalServer;
    }

    @Override
    public GrpcServer start() throws IOException {
        return new DefaultGrpcServer(internalServer.start());
    }

    @Override
    public GrpcServer shutdown() {
        return new DefaultGrpcServer(internalServer.shutdown());
    }

    @Override
    public GrpcServer shutdownNow() {
        return new DefaultGrpcServer(internalServer.shutdownNow());
    }

    @Override
    public boolean isShutdown() {
        return internalServer.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return internalServer.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return internalServer.awaitTermination(timeout, unit);
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        internalServer.awaitTermination();
    }

    @Override
    public int getPort() {
        return internalServer.getPort();
    }

    @Override
    public List<? extends SocketAddress> getListenSockets() {
        return internalServer.getListenSockets();
    }

    @Override
    public List<ServerServiceDefinition> getServices() {
        return internalServer.getServices();
    }

    @Override
    public List<ServerServiceDefinition> getImmutableServices() {
        return internalServer.getImmutableServices();
    }

    @Override
    public List<ServerServiceDefinition> getMutableServices() {
        return internalServer.getMutableServices();
    }

    public static GrpcServer.Builder builder(int port) {
        DefaultBuilder builder = new DefaultBuilder();
        return builder.forPort(port);
    }

    private static class DefaultBuilder implements GrpcServer.Builder {
        private ServerBuilder<?> internalBuilder = ServerBuilder.forPort(8080);

        @Override
        public Builder forPort(int port) {
            this.internalBuilder = ServerBuilder.forPort(port);
            return this;
        }

        @Override
        public Builder addService(BindableService service) {
            internalBuilder.addService(service);
            return this;
        }

        @Override
        public Builder addService(ServerServiceDefinition service) {
            internalBuilder.addService(service);
            return this;
        }

        @Override
        public Builder addServices(List<ServerServiceDefinition> services) {
            internalBuilder.addServices(services);
            return this;
        }

        @Override
        public Builder addStreamTracerFactory(ServerStreamTracer.Factory factory) {
            internalBuilder.addStreamTracerFactory(factory);
            return this;
        }

        @Override
        public Builder addTransportFilter(ServerTransportFilter filter) {
            internalBuilder.addTransportFilter(filter.unwrap());
            return this;
        }

        @Override
        public GrpcServer build() {
            return new DefaultGrpcServer(internalBuilder.build());
        }

        @Override
        public Builder callExecutor(ServerCallExecutorSupplier executorSupplier) {
            internalBuilder.callExecutor(executorSupplier);
            return this;
        }

        @Override
        public Builder compressorRegistry(CompressorRegistry registry) {
            internalBuilder.compressorRegistry(registry);
            return this;
        }

        @Override
        public Builder decompressorRegistry(DecompressorRegistry registry) {
            internalBuilder.decompressorRegistry(registry);
            return this;
        }

        @Override
        public Builder executor(Executor executor) {
            internalBuilder.executor(executor);
            return this;
        }

        @Override
        public Builder fallbackHandlerRegistry(HandlerRegistry fallbackRegistry) {
            internalBuilder.fallbackHandlerRegistry(fallbackRegistry);
            return this;
        }

        @Override
        public Builder handshakeTimeout(long timeout, TimeUnit unit) {
            internalBuilder.handshakeTimeout(timeout, unit);
            return this;
        }

        @Override
        public Builder intercept(ServerInterceptor interceptor) {
            internalBuilder.intercept(interceptor);
            return this;
        }

        @Override
        public Builder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
            internalBuilder.keepAliveTime(keepAliveTime, timeUnit);
            return this;
        }

        @Override
        public Builder keepAliveTimeout(long keepAliveTimeout, TimeUnit timeUnit) {
            internalBuilder.handshakeTimeout(keepAliveTimeout, timeUnit);
            return this;
        }

        @Override
        public Builder maxConnectionIdle(long maxConnectionIdle, TimeUnit timeUnit) {
            internalBuilder.maxConnectionIdle(maxConnectionIdle, timeUnit);
            return this;
        }

        @Override
        public Builder useTransportSecurity(File certChain, File privateKey) {
            internalBuilder.useTransportSecurity(certChain, privateKey);
            return this;
        }

        @Override
        public Builder useTransportSecurity(InputStream certChain, InputStream privateKey) {
            internalBuilder.useTransportSecurity(certChain, privateKey);
            return this;
        }

        @Override
        public Builder useTransportSecurity(String certChain, String privateKey) {
            internalBuilder.useTransportSecurity(Path.of(certChain).toFile(), Path.of(privateKey).toFile());
            return this;
        }
    }
}
