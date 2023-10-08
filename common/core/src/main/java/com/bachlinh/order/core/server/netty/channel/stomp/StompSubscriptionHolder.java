package com.bachlinh.order.core.server.netty.channel.stomp;

import java.util.List;

public interface StompSubscriptionHolder {
    List<StompSubscription> getSubscriptions(String destination);
}
