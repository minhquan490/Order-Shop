package com.bachlinh.order.core.http.server.channel.stomp.publisher;

public interface NotificationPublisher {
    void pushNotification(Object message);
}
