package com.bachlinh.order.http.server.channel.stomp;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;

import com.bachlinh.order.core.exception.system.server.StompProtocolException;
import com.bachlinh.order.http.server.listener.StompFrameListener;

import java.nio.charset.StandardCharsets;

import org.springframework.util.StringUtils;

public class StompHandler extends SimpleChannelInboundHandler<StompFrame> {

    private final StompFrameListener stompFrameListener;

    public StompHandler(StompFrameListener stompFrameListener) {
        this.stompFrameListener = stompFrameListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StompFrame msg) {
        DecoderResult decoderResult = msg.decoderResult();
        if (decoderResult.isFailure()) {
            throw new StompProtocolException("Handle failure", "Can not process message");
        }

        switch (msg.command()) {
            case StompCommand.STOMP -> {/* Do nothing*/}
            case StompCommand.CONNECT -> stompFrameListener.onConnect(ctx, msg);
            case StompCommand.SUBSCRIBE -> stompFrameListener.onSubscribe(ctx, msg);
            case StompCommand.SEND -> stompFrameListener.onSend(ctx, msg);
            case StompCommand.UNSUBSCRIBE -> stompFrameListener.onUnsubscribe(ctx, msg);
            case StompCommand.DISCONNECT -> stompFrameListener.onDisconnect(ctx, msg);
            default -> throw new StompProtocolException("Invalid type message", "Unknown message type");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof StompProtocolException stompProtocolException) {
            sendErrorFrame(stompProtocolException.getMessage(), stompProtocolException.getDescription(), ctx);
            return;
        }
        ctx.close();
    }

    private void sendErrorFrame(String message, String description, ChannelHandlerContext ctx) {
        StompFrame errorFrame = new DefaultStompFrame(StompCommand.ERROR);
        errorFrame.headers().set(StompHeaders.MESSAGE, message);

        if (StringUtils.hasText(description)) {
            errorFrame.content().writeCharSequence(description, StandardCharsets.UTF_8);
        }

        ctx.writeAndFlush(errorFrame).addListener(ChannelFutureListener.CLOSE);
    }
}
