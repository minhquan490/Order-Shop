package com.bachlinh.order.handler.tcp.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import com.bachlinh.order.handler.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.utils.JacksonUtils;

import java.security.Principal;

public abstract class SpringWebSocketHandler implements WebSocketHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final WebSocketSessionManager socketSessionManager;

    protected SpringWebSocketHandler(WebSocketSessionManager socketSessionManager) {
        this.socketSessionManager = socketSessionManager;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        socketSessionManager.openConnection(session);
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Object payload = message.getPayload();
        if (payload instanceof String string) {
            JsonNode node = JacksonUtils.getSingleton().readTree(string);
            doBeforeSendMessage(session, node);
            socketSessionManager.handleInComingMessage(message, node.get(WebSocketSessionManager.RECEIVER_KEY).asText());
            doAfterSendMessage(session, node);
            return;
        }
        if (payload instanceof byte[] bytes) {
            JsonNode node = JacksonUtils.getSingleton().readTree(bytes);
            doBeforeSendMessage(session, node);
            socketSessionManager.handleInComingMessage(message, node.get(WebSocketSessionManager.RECEIVER_KEY).asText());
            doAfterSendMessage(session, node);
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        log.error("Transport message error, close connection", exception);
        afterConnectionClosed(session, CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @Nullable CloseStatus closeStatus) throws Exception {
        Principal principal = session.getPrincipal();
        if (principal != null) {
            socketSessionManager.disconnect(principal.getName());
        } else {
            socketSessionManager.clearDisconnectConnection();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    protected abstract void doBeforeSendMessage(@NonNull WebSocketSession session, JsonNode convertedMessage);

    protected abstract void doAfterSendMessage(@NonNull WebSocketSession session, JsonNode convertedMessage);
}
