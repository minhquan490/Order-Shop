package com.bachlinh.order.core.http.server.channel.stomp;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class NettySocketConnectionHolder implements StompSubscriptionOperation<Channel>, StompSubscriptionHolder {
    public static final Comparator<StompSubscription> SUBSCRIPTION_COMPARATOR = Comparator.comparing(StompSubscription::userId);

    private final Map<String, List<StompSubscription>> destinationsMap = new ConcurrentHashMap<>();

    @Override
    public List<StompSubscription> getSubscriptions(String destination) {
        List<StompSubscription> destinations = destinationsMap.get(destination);
        return Objects.requireNonNullElse(destinations, Collections.emptyList());
    }

    @Override
    public void subscribe(StompSubscription stompSubscription) {
        destinationsMap.compute(stompSubscription.destination(), (s, stompSubscriptions) -> {
            if (stompSubscriptions == null) {
                stompSubscriptions = createList();
            }
            stompSubscriptions.add(stompSubscription);
            stompSubscriptions.sort(SUBSCRIPTION_COMPARATOR);
            return stompSubscriptions;
        });
    }

    @Override
    public void unSubscribe(StompSubscription stompSubscription) {
        destinationsMap.compute(stompSubscription.destination(), (s, stompSubscriptions) -> {
            if (stompSubscriptions == null || stompSubscriptions.isEmpty()) {
                return createList();
            }
            stompSubscriptions.remove(stompSubscription);
            return stompSubscriptions;
        });
    }

    @Override
    public void unSubscribe(String subscribeId, String destination, Channel channel) {
        Collection<StompSubscription> stompSubscriptions = getSubscriptions(destination);
        for (StompSubscription stompSubscription : stompSubscriptions) {
            if (stompSubscription.id().equals(subscribeId) && stompSubscription.channel().id().equals(channel.id())) {
                unSubscribe(stompSubscription);
            }
        }
    }

    private List<StompSubscription> createList() {
        return new ArrayList<>();
    }
}
