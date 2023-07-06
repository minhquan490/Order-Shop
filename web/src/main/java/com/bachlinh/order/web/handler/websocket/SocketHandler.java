package com.bachlinh.order.web.handler.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.WebSocketSession;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.handler.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.handler.tcp.handler.SpringWebSocketHandler;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.DirectMessageRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Executor;

public class SocketHandler extends SpringWebSocketHandler {
    private final DependenciesResolver resolver;

    private DirectMessageRepository directMessageRepository;
    private CustomerRepository customerRepository;
    private EntityFactory entityFactory;
    private Executor executor;

    public SocketHandler(WebSocketSessionManager socketSessionManager, DependenciesResolver resolver) {
        super(socketSessionManager);
        this.resolver = resolver;
    }

    @Override
    protected void doBeforeSendMessage(@NonNull WebSocketSession session, JsonNode convertedMessage) {
        inject();
    }

    @Override
    protected void doAfterSendMessage(@NonNull WebSocketSession session, JsonNode convertedMessage) {
        Runnable runnable = () -> {
            String receiver = convertedMessage.get(WebSocketSessionManager.RECEIVER_KEY).asText();
            String sender = Objects.requireNonNull(session.getPrincipal()).getName();
            DirectMessage message = entityFactory.getEntity(DirectMessage.class);
            Customer customerReceive = customerRepository.getCustomerById(receiver, false);
            Customer customerSend = customerRepository.getCustomerById(sender, false);
            message.setToCustomer(customerReceive);
            message.setFromCustomer(customerSend);
            message.setContent(convertedMessage.get(WebSocketSessionManager.CONTENT_KEY).asText());
            message.setTimeSent(Timestamp.from(Instant.now()));
            directMessageRepository.saveMessage(message);
        };
        executor.execute(runnable);
    }

    private void inject() {
        if (directMessageRepository == null) {
            directMessageRepository = resolver.resolveDependencies(DirectMessageRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = resolver.resolveDependencies(EntityFactory.class);
        }
        if (executor == null) {
            executor = resolver.resolveDependencies(ThreadPoolTaskExecutor.class);
        }
        if (customerRepository == null) {
            customerRepository = resolver.resolveDependencies(CustomerRepository.class);
        }
    }
}
