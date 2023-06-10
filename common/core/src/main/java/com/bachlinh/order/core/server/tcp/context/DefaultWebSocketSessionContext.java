package com.bachlinh.order.core.server.tcp.context;

import org.springframework.web.socket.WebSocketSession;
import com.bachlinh.order.core.server.tcp.messaging.JsonMessage;

import java.io.IOException;
import java.net.InetSocketAddress;

class DefaultWebSocketSessionContext implements WebSocketSessionContext {
    private final WebSocketSession session;
    private final String userId;
    private final String role;
    private final String clientSecret;
    private final boolean isAdmin;

    DefaultWebSocketSessionContext(WebSocketSession session, String userId, String role, String clientSecret, boolean isAdmin) {
        this.session = session;
        this.userId = userId;
        this.role = role;
        this.clientSecret = clientSecret;
        this.isAdmin = isAdmin;
    }

    @Override
    public String getClientSecret() {
        return this.clientSecret;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public String getRole() {
        return this.role;
    }

    @Override
    public String getSessionUnique() {
        return this.session.getId();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.session.getRemoteAddress();
    }

    @Override
    public void sendMessage(Object message) throws IOException {
        this.session.sendMessage(JsonMessage.convert(message));
    }

    @Override
    public void close() throws IOException {
        if (session.isOpen()) {
            this.session.close();
        }
    }

    @Override
    public boolean isAdminConnection() {
        return isAdmin;
    }

    @Override
    public boolean isClosed() {
        return !session.isOpen();
    }
}
