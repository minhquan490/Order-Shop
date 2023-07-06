package com.bachlinh.order.handler.tcp.context;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface WebSocketSessionManager {
    static String RECEIVER_KEY = "receiver";
    static String CONTENT_KEY = "content";

    WebSocketSessionContext getSocketContext(String userId);

    void handleInComingMessage(WebSocketMessage<?> message, String receiver) throws IOException;

    int getTotalClientConnected();

    void disconnect(String userId) throws IOException;

    void openConnection(WebSocketSession session);

    void pushMessage(Object message, String receiver) throws IOException;

    void pushMessageToAllAdmin(Object message) throws IOException;

    void clearDisconnectConnection();
}