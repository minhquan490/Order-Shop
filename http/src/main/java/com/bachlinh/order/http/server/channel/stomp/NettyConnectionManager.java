package com.bachlinh.order.http.server.channel.stomp;

import com.google.common.net.MediaType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;

import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.core.exception.system.server.StompProtocolException;
import com.bachlinh.order.http.server.channel.stomp.publisher.NotificationPublisher;
import com.bachlinh.order.core.utils.JacksonUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NettyConnectionManager extends AbstractConnectionManager<Channel> implements NotificationPublisher {

    private String toCustomerHeader;
    private String subscribePath;

    public NettyConnectionManager(NettySocketConnectionHolder connectionHolder) {
        this(connectionHolder, connectionHolder);
    }

    public NettyConnectionManager(StompSubscriptionOperation<Channel> operation, StompSubscriptionHolder holder) {
        super(operation, holder);
    }

    @Override
    public void sendMessage(String destination, String toCustomer, Object message) {
        List<StompSubscription> subscriptions = getHolder().getSubscriptions(destination);
        StompSubscription dummy = new StompSubscription("", toCustomer, "", "", null);
        int position = Collections.binarySearch(subscriptions, dummy, NettySocketConnectionHolder.SUBSCRIPTION_COMPARATOR);

        if (position < 0) {
            String m = StringTemplate.STR. "Customer with id [\{ toCustomer }] not online" ;
            throw new StompProtocolException("Not found", m);
        }

        StompSubscription stompSubscription = subscriptions.get(position);
        StompFrame transformedMessage;
        if (message instanceof StompFrame inboundFrame) {
            transformedMessage = transformToMessage(inboundFrame, stompSubscription);
        } else {
            transformedMessage = convertMessage(message, stompSubscription);
        }

        stompSubscription.channel().writeAndFlush(transformedMessage);
    }

    @Override
    public void sendMessage(String destination, Object message) {
        List<StompSubscription> subscriptions = getHolder().getSubscriptions(destination);
        for (StompSubscription stompSubscription : subscriptions) {
            sendMessage(destination, stompSubscription.userId(), message);
        }
    }

    @Override
    public void unSubscribe(String subscribeId, String destination, Channel channel) {
        decrement();
        getOperation().unSubscribe(subscribeId, destination, channel);
    }

    public String getToCustomerHeader() {
        if (toCustomerHeader == null) {
            toCustomerHeader = getEnvironment().getProperty("server.header.to.customer");
        }
        return toCustomerHeader;
    }

    public String getSubscribePath() {
        if (subscribePath == null) {
            subscribePath = getEnvironment().getProperty("server.stomp.subscribe.path");
        }
        return subscribePath;
    }

    @Override
    public void pushNotification(Object message) {
        List<StompSubscription> subscriptions = getHolder().getSubscriptions(getSubscribePath());
        List<StompSubscription> adminSubscriptions = subscriptions.stream()
                .filter(stompSubscription -> stompSubscription.role().equalsIgnoreCase(Role.ADMIN.name()))
                .toList();
        for (StompSubscription subscription : adminSubscriptions) {
            sendMessage(getSubscribePath(), subscription.userId(), message);
        }
    }

    private StompFrame transformToMessage(StompFrame sendFrame, StompSubscription subscription) {
        StompFrame messageFrame = new DefaultStompFrame(StompCommand.MESSAGE, sendFrame.content().retainedDuplicate());
        String id = UUID.randomUUID().toString();
        messageFrame.headers()
                .set(StompHeaders.MESSAGE_ID, id)
                .set(StompHeaders.SUBSCRIPTION, subscription.id())
                .set(StompHeaders.CONTENT_LENGTH, Integer.toString(messageFrame.content().readableBytes()));
        CharSequence contentType = sendFrame.headers().get(StompHeaders.CONTENT_TYPE);
        if (contentType != null) {
            messageFrame.headers().set(StompHeaders.CONTENT_TYPE, contentType);
        }

        return messageFrame;
    }

    private StompFrame convertMessage(Object message, StompSubscription stompSubscription) {
        String json = JacksonUtils.writeObjectAsString(message);
        ByteBuf data = Unpooled.wrappedBuffer(json.getBytes(StandardCharsets.UTF_8));
        StompFrame transformedMessage = new DefaultStompFrame(StompCommand.MESSAGE, data);
        String id = UUID.randomUUID().toString();
        transformedMessage.headers()
                .set(StompHeaders.MESSAGE_ID, id)
                .set(StompHeaders.SUBSCRIPTION, stompSubscription.id())
                .set(StompHeaders.CONTENT_LENGTH, Integer.toString(data.readableBytes()))
                .set(StompHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.type());
        return transformedMessage;
    }
}
