package com.bachlinh.order.core.tcp.context;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface WebSocketSessionContext {
    String getClientSecret();

    String getUserId();

    String getRole();

    String getSessionUnique();

    InetSocketAddress getRemoteAddress();

    void sendMessage(Object message) throws IOException;

    void close() throws IOException;

    boolean isAdminConnection();

    boolean isClosed();
}
