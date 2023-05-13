package com.bachlinh.order.security.filter;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import com.bachlinh.order.core.server.grpc.InboundMessage;
import com.bachlinh.order.core.server.grpc.OutboundMessage;
import com.bachlinh.order.core.server.grpc.ServerInterceptor;
import com.bachlinh.order.core.server.grpc.adapter.ServletRequestAdapter;
import com.bachlinh.order.core.server.grpc.adapter.ServletResponseAdapter;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.security.helper.AuthenticationHelper;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.io.IOException;
import java.util.UUID;

public abstract class GrpcWebFilter implements ServerInterceptor {
    private final DependenciesResolver dependenciesResolver;
    private final Environment environment;

    protected GrpcWebFilter(DependenciesResolver dependenciesResolver, String profile) {
        this.dependenciesResolver = dependenciesResolver;
        this.environment = Environment.getInstance(profile);
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
                        HttpServletResponse response = new ServletResponseAdapter(castedCall);
                        AuthenticationHelper.holdResponse(m.getRequestId(), response);
                        try {
                            intercept(ServletRequestAdapter.wrap(m), response, castedCall);
                        } catch (IOException e) {
                            call.close(Status.CANCELLED, new Metadata());
                            return;
                        }
                    }
                }
                super.onMessage(message);
            }
        };
    }

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected abstract boolean shouldNotFilter(@NonNull HttpServletRequest request);

    protected abstract <U> void intercept(HttpServletRequest request, HttpServletResponse response, ServerCall<InboundMessage, U> call) throws IOException;

    protected abstract void inject();
}
