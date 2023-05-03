package com.bachlinh.order.web.handler.websocket;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketMessage;
import com.bachlinh.order.core.tcp.context.AbstractWebSocketSessionManager;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.security.auth.spi.PrincipalHolder;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.io.IOException;

public class WebSocketManager extends AbstractWebSocketSessionManager {

    private final DependenciesResolver resolver;

    public WebSocketManager(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected String queryUserId() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customer.getId();
    }

    @Override
    protected String queryRole() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customer.getRole();
    }

    @Override
    protected String queryClientSecret() {
        PrincipalHolder principalHolder = (PrincipalHolder) SecurityContextHolder.getContext().getAuthentication();
        return principalHolder.getClientSecret();
    }

    @Override
    public void handleInComingMessage(WebSocketMessage<?> message, String receiver) throws IOException {
        getSocketContext(receiver).sendMessage(message.getPayload());
    }

    @Override
    public void pushMessage(Object message, String receiver) throws IOException {
        getSocketContext(receiver).sendMessage(message);
    }
}
