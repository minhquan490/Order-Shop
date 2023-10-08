package com.bachlinh.order.core.server.netty.channel.stomp.publisher;

public interface NotificationPublisher {
    void pushNotification(Object message);
}
