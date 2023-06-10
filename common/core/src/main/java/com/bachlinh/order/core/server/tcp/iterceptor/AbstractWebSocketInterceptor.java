package com.bachlinh.order.core.server.tcp.iterceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Map;

@Slf4j
public abstract class AbstractWebSocketInterceptor implements HandshakeInterceptor {
    private final DependenciesResolver dependenciesResolver;

    protected AbstractWebSocketInterceptor(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
    }


    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Map<String, Object> attributes) throws Exception {
        inject();
        return authorizeHandshakeRequest(request, response);
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
        inject();
        if (exception != null) {
            log.info("Close socket connection because [{}]", exception.getClass().getName());
        }
    }

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected abstract boolean authorizeHandshakeRequest(ServerHttpRequest request, ServerHttpResponse response);

    protected abstract void inject();
}
