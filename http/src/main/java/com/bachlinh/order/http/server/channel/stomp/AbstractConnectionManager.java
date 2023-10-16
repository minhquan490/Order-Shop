package com.bachlinh.order.http.server.channel.stomp;

import com.bachlinh.order.core.environment.Environment;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractConnectionManager<T> implements StompConnectionManager<T> {

    private final AtomicInteger totalConnection = new AtomicInteger(0);

    private final StompSubscriptionOperation<T> operation;
    private final StompSubscriptionHolder holder;

    private Environment environment;

    protected AbstractConnectionManager(StompSubscriptionOperation<T> operation, StompSubscriptionHolder holder) {
        this.operation = operation;
        this.holder = holder;
    }

    @Override
    public int totalClientConnected() {
        return totalConnection.get();
    }

    @Override
    public List<StompSubscription> getSubscriptions(String destination) {
        return holder.getSubscriptions(destination);
    }

    @Override
    public void subscribe(StompSubscription stompSubscription) {
        increment();
        operation.subscribe(stompSubscription);
    }

    @Override
    public void unSubscribe(StompSubscription stompSubscription) {
        decrement();
        operation.unSubscribe(stompSubscription);
    }

    protected void increment() {
        totalConnection.incrementAndGet();
    }

    protected void decrement() {
        totalConnection.decrementAndGet();
    }

    protected StompSubscriptionHolder getHolder() {
        return holder;
    }

    protected StompSubscriptionOperation<T> getOperation() {
        return operation;
    }

    public Environment getEnvironment() {
        if (environment == null) {
            String currentEnvironment = Environment.getMainEnvironmentName();
            environment = Environment.getInstance(currentEnvironment);
        }
        return environment;
    }
}
