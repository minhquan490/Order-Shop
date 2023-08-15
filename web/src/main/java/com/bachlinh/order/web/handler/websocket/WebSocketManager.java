package com.bachlinh.order.web.handler.websocket;

import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.tcp.context.AbstractWebSocketSessionManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketMessage;

import java.io.IOException;
import java.util.Collection;

public class WebSocketManager extends AbstractWebSocketSessionManager {

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
        return "";
    }

    @Override
    protected boolean isAdmin() {
        return queryRole().equalsIgnoreCase(Role.ADMIN.name());
    }

    @Override
    public void handleInComingMessage(WebSocketMessage<?> message, String receiver) throws IOException {
        getSocketContext(receiver).sendMessage(message.getPayload());
    }

    @Override
    public void pushMessage(Object message, String receiver) throws IOException {
        getSocketContext(receiver).sendMessage(message);
    }

    @Override
    public void pushMessageToAllAdmin(Object message) throws IOException {
        Collection<String> adminIds = getAllAdminConnection();
        for (String id : adminIds) {
            pushMessage(message, id);
        }
    }
}
