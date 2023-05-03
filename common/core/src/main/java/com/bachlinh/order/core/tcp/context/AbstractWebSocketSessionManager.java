package com.bachlinh.order.core.tcp.context;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractWebSocketSessionManager implements WebSocketSessionManager {

    private final Map<String, WebSocketSessionContext> connectedClient = new ConcurrentHashMap<>();

    @Override
    public WebSocketSessionContext getSocketContext(String username) {
        return connectedClient.get(username);
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
        addConnection(session, userId, role, clientSecret);
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

    protected void addConnection(WebSocketSession session, String userId, String role, String clientSecret) {
        WebSocketSessionContext context = new DefaultWebSocketSessionContext(session, userId, role, clientSecret);
        connectedClient.put(userId, context);
    }

    protected abstract String queryUserId();

    protected abstract String queryRole();

    protected abstract String queryClientSecret();
}
