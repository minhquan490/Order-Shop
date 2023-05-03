package com.bachlinh.order.core.tcp.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import com.bachlinh.order.core.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.utils.JacksonUtils;

import java.security.Principal;

public class SpringWebSocketHandler implements WebSocketHandler {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(SpringWebSocketHandler.class);
    private final WebSocketSessionManager socketSessionManager;

    public SpringWebSocketHandler(WebSocketSessionManager socketSessionManager) {
        this.socketSessionManager = socketSessionManager;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        socketSessionManager.openConnection(session);
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Object payload = message.getPayload();
        if (payload instanceof String string) {
            JsonNode node = JacksonUtils.getSingleton().readTree(string);
            socketSessionManager.handleInComingMessage(message, node.get("receiver").asText());
            return;
        }
        if (payload instanceof byte[] bytes) {
            JsonNode node = JacksonUtils.getSingleton().readTree(bytes);
            socketSessionManager.handleInComingMessage(message, node.get("receiver").asText());
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        log.error("Transport message error, close connection", exception);
        afterConnectionClosed(session, null);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @Nullable CloseStatus closeStatus) throws Exception {
        Principal principal = session.getPrincipal();
        String identifier;
        if (principal != null) {
            identifier = principal.getName();
        } else {
            identifier = session.getId();
        }
        socketSessionManager.disconnect(identifier);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
