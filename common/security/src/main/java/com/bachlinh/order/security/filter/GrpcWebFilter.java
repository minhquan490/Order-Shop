package com.bachlinh.order.security.filter;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.server.grpc.InboundMessage;
import com.bachlinh.order.core.server.grpc.OutboundMessage;
import com.bachlinh.order.core.server.grpc.ServerInterceptor;
import com.bachlinh.order.core.server.grpc.adapter.ServletRequestAdapter;
import com.bachlinh.order.core.server.grpc.adapter.ServletResponseAdapter;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.security.helper.AuthenticationHelper;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public abstract class GrpcWebFilter implements ServerInterceptor {
    private final DependenciesResolver dependenciesResolver;
    private final Environment environment;
    private final RepositoryManager repositoryManager;

    protected GrpcWebFilter(DependenciesResolver dependenciesResolver, String profile) {
        this.dependenciesResolver = dependenciesResolver;
        this.environment = Environment.getInstance(profile);
        this.repositoryManager = resolveDependencies(RepositoryManager.class);
    }

    @Override
    public final <T, U> ServerCall.Listener<T> interceptCall(ServerCall<T, U> call, Metadata headers, ServerCallHandler<T, U> next) {
        inject();
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(next.startCall(call, headers)) {
            @Override
            public void onMessage(T message) {
                if (message instanceof InboundMessage m) {
                    @SuppressWarnings("unchecked")
                    ServerCall<InboundMessage, OutboundMessage> castedCall = (ServerCall<InboundMessage, OutboundMessage>) call;
                    m = m.toBuilder().setRequestId(UUID.randomUUID().toString()).build();
                    if (!shouldNotFilter(ServletRequestAdapter.wrap(m))) {
                        HttpServletResponse response = AuthenticationHelper.getResponse(m.getRequestId());
                        if (response == null) {
                            response = new ServletResponseAdapter(castedCall);
                            AuthenticationHelper.holdResponse(m.getRequestId(), response);
                        }
                        try {
                            intercept(ServletRequestAdapter.wrap(m), response);
                        } catch (IOException e) {
                            call.close(Status.CANCELLED, new Metadata());
                            AuthenticationHelper.release(m.getRequestId());
                            return;
                        }
                    }
                }
                super.onMessage(message);
            }
        };
    }

    public abstract GrpcWebFilter setExcludePaths(Collection<String> excludePaths);

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected <T> T resolveRepository(Class<T> repositoryType) {
        return this.repositoryManager.getRepository(repositoryType);
    }

    protected <T> T resolveDependencies(Class<T> dependenciesType) {
        return this.dependenciesResolver.resolveDependencies(dependenciesType);
    }

    protected abstract boolean shouldNotFilter(@NonNull HttpServletRequest request);

    protected abstract void intercept(HttpServletRequest request, HttpServletResponse response) throws IOException;

    protected abstract void inject();
}
