package com.bachlinh.order.web.common.listener;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.DefaultStompHeaders;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;

import com.bachlinh.order.core.exception.system.server.StompProtocolException;
import com.bachlinh.order.http.server.channel.stomp.NettyConnectionManager;
import com.bachlinh.order.http.server.channel.stomp.StompSubscription;
import com.bachlinh.order.http.server.channel.stomp.StompVersion;
import com.bachlinh.order.http.server.listener.StompFrameListener;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.web.repository.spi.CustomerRepository;
import com.bachlinh.order.security.auth.spi.TokenManager;

import java.util.HashMap;
import java.util.Map;

public class NettyWebSocketFrameListener implements StompFrameListener {

    private final NettyConnectionManager nettyConnectionManager;
    private final String accessTokenHeader;
    private final String refreshTokenHeader;
    private final TokenManager tokenManager;
    private final CustomerRepository customerRepository;

    public NettyWebSocketFrameListener(CustomerRepository customerRepository, NettyConnectionManager nettyConnectionManager, TokenManager tokenManager, String accessTokenHeader, String refreshTokenHeader) {
        this.nettyConnectionManager = nettyConnectionManager;
        this.accessTokenHeader = accessTokenHeader;
        this.refreshTokenHeader = refreshTokenHeader;
        this.tokenManager = tokenManager;
        this.customerRepository = customerRepository;
    }

    @Override
    public void onConnect(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        StompHeaders headers = validateBeforeConnect(inboundFrame);
        if (headers == null) {
            throw new StompProtocolException("UnAuthorize !", "Please login before connect");
        } else {
            String acceptVersion = headers.getAsString(StompHeaders.ACCEPT_VERSION);
            StompVersion handshakeVersion = ctx.channel().attr(StompVersion.CHANNEL_ATTRIBUTE_KEY).get();
            if (acceptVersion == null || !acceptVersion.contains(handshakeVersion.version())) {
                throw new StompProtocolException("Invalid message", "Received invalid version, expected: " + handshakeVersion.version());
            }
            StompFrame connectedFrame = new DefaultStompFrame(StompCommand.CONNECTED);
            connectedFrame.headers()
                    .set(StompHeaders.VERSION, handshakeVersion.version())
                    .set(StompHeaders.SERVER, "Netty-server")
                    .set(StompHeaders.HEART_BEAT, "5000,5000")
                    .set(accessTokenHeader, headers.getAsString(accessTokenHeader))
                    .set(refreshTokenHeader, headers.getAsString(refreshTokenHeader));
            ctx.writeAndFlush(connectedFrame);
        }
    }

    @Override
    public void onSubscribe(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        String destination = inboundFrame.headers().getAsString(StompHeaders.DESTINATION);
        String id = inboundFrame.headers().getAsString(StompHeaders.ID);
        if (destination == null || id == null) {
            throw new StompProtocolException("Missed header", "Required 'destination' or 'id' header missed");
        }

        final StompSubscription stompSubscription = createSubscription(ctx, inboundFrame, destination, id);
        nettyConnectionManager.subscribe(stompSubscription);
        ctx.channel().closeFuture().addListener((ChannelFutureListener) future -> nettyConnectionManager.unSubscribe(stompSubscription));

        String receiptId = inboundFrame.headers().getAsString(StompHeaders.RECEIPT);
        if (receiptId != null) {
            StompFrame receiptFrame = new DefaultStompFrame(StompCommand.RECEIPT);
            receiptFrame.headers().set(StompHeaders.RECEIPT_ID, receiptId);
            ctx.writeAndFlush(receiptFrame);
        }
    }

    @Override
    public void onSend(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        String destination = inboundFrame.headers().getAsString(StompHeaders.DESTINATION);
        String toCustomer = inboundFrame.headers().getAsString(nettyConnectionManager.getToCustomerHeader());
        if (destination == null) {
            throw new StompProtocolException("Missed header", "Require 'destination' header missed");
        }

        nettyConnectionManager.sendMessage(destination, toCustomer, inboundFrame);
    }

    @Override
    public void onUnsubscribe(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        String subscriptionId = inboundFrame.headers().getAsString(StompHeaders.SUBSCRIPTION);
        String destination = inboundFrame.headers().getAsString(StompHeaders.DESTINATION);
        nettyConnectionManager.unSubscribe(subscriptionId, destination, ctx.channel());
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx, StompFrame inboundFrame) {
        String receiptId = inboundFrame.headers().getAsString(StompHeaders.RECEIPT);
        if (receiptId == null) {
            ctx.close();
            return;
        }

        StompFrame receiptFrame = new DefaultStompFrame(StompCommand.RECEIPT);
        receiptFrame.headers().set(StompHeaders.RECEIPT_ID, receiptId);
        ctx.writeAndFlush(receiptFrame).addListener(ChannelFutureListener.CLOSE);
    }

    private StompHeaders validateBeforeConnect(StompFrame inboundFrame) {
        String jwt = extractJwt(inboundFrame);
        String refreshToken = extractRefreshToken(inboundFrame);
        Map<String, Object> claims = parse(jwt, refreshToken, 0);
        if (claims.isEmpty()) {
            return null;
        } else {
            StompHeaders stompHeaders = inboundFrame.headers();
            DefaultStompHeaders cloned = ((DefaultStompHeaders) stompHeaders).copy();
            cloned.set(accessTokenHeader, jwt);
            cloned.set(refreshToken, refreshToken);
            return cloned;
        }
    }

    private String extractJwt(StompFrame inboundFrame) {
        StompHeaders stompHeaders = inboundFrame.headers();
        return stompHeaders.getAsString(accessTokenHeader);
    }

    private String extractRefreshToken(StompFrame inboundFrame) {
        StompHeaders stompHeaders = inboundFrame.headers();
        return stompHeaders.getAsString(refreshTokenHeader);
    }

    private Map<String, Object> parse(String jwt, String refreshToken, int count) {
        Map<String, Object> result = tokenManager.getClaimsFromToken(jwt);
        if ((result.isEmpty() && refreshToken != null) || (count != 1 && refreshToken != null)) {
            jwt = tokenManager.revokeAccessToken(refreshToken);
            count++;
            return parse(jwt, refreshToken, count);
        } else {
            return HashMap.newHashMap(0);
        }
    }

    private StompSubscription createSubscription(ChannelHandlerContext ctx, StompFrame inboundFrame, String destination, String id) {
        String jwt = extractJwt(inboundFrame);
        String refreshToken = extractRefreshToken(inboundFrame);
        Map<String, Object> claims = parse(jwt, refreshToken, 0);
        String userId = (String) claims.get(Customer_.ID);
        Customer customer = customerRepository.getCustomerBasicInformation(userId);
        if (userId == null || customer == null) {
            throw new StompProtocolException("UnAuthorize", "Please login and try again");
        }
        return new StompSubscription(id, customer.getId(), customer.getRole(), destination, ctx.channel());
    }
}
