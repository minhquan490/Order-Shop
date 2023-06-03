package com.bachlinh.order.core.server.tcp.context;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractWebSocketSessionManager implements WebSocketSessionManager {

    private final Map<String, WebSocketSessionContext> connectedClient = new ConcurrentHashMap<>();

    @Override
    public WebSocketSessionContext getSocketContext(String userId) {
        return connectedClient.get(userId);
    }

    @Override
    public int getTotalClientConnected() {
        return connectedClient.size();
    }

    @Override
    public void openConnection(WebSocketSession session) {
        String userId = queryUserId();
        String role = queryRole();
        String clientSecret = queryClientSecret();
        boolean isAdmin = isAdmin();
        addConnection(session, userId, role, clientSecret, isAdmin);
    }

    @Override
    public void disconnect(String userId) throws IOException {
        WebSocketSessionContext socketSessionContext = connectedClient.get(userId);
        if (socketSessionContext != null) {
            socketSessionContext.close();
            connectedClient.remove(userId, socketSessionContext);
        } else {
            var iterator = connectedClient.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (entry.getValue().getSessionUnique().equals(userId)) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    @Override
    public void clearDisconnectConnection() {
        connectedClient.entrySet().removeIf(entry -> entry.getValue().isClosed());
    }

    protected void addConnection(WebSocketSession session, String userId, String role, String clientSecret, boolean isAdmin) {
        WebSocketSessionContext context = new DefaultWebSocketSessionContext(session, userId, role, clientSecret, isAdmin);
        connectedClient.put(userId, context);
    }

    protected Collection<String> getAllAdminConnection() {
        return connectedClient.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isAdminConnection())
                .map(Map.Entry::getKey)
                .toList();
    }

    protected abstract String queryUserId();

    protected abstract String queryRole();

    protected abstract String queryClientSecret();

    protected abstract boolean isAdmin();
}
