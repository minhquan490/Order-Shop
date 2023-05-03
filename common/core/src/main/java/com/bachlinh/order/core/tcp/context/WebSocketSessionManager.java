package com.bachlinh.order.core.tcp.context;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface WebSocketSessionManager {

    WebSocketSessionContext getSocketContext(String username);

    void handleInComingMessage(WebSocketMessage<?> message, String receiver) throws IOException;

    int getTotalClientConnected();

    void disconnect(String userId) throws IOException;

    void openConnection(WebSocketSession session);

    void pushMessage(Object message, String receiver) throws IOException;
}