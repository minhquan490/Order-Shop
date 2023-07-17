package com.bachlinh.order.core.server.netty.channel.codec;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.DecoratingHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2LifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Http2ControlFrameLimitEncoder extends DecoratingHttp2ConnectionEncoder {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final int maxFrameLimit;
    private Http2LifecycleManager lifecycleManager;
    private int outstandingControlFrames;
    private boolean limitReached;

    private final ChannelFutureListener outstandingControlFramesListener = future -> outstandingControlFrames--;

    public Http2ControlFrameLimitEncoder(Http2ConnectionEncoder delegate) {
        this(delegate, Http2CodecUtil.DEFAULT_MAX_QUEUED_CONTROL_FRAMES);
    }

    public Http2ControlFrameLimitEncoder(Http2ConnectionEncoder delegate, int maxFrameLimit) {
        super(delegate);
        this.maxFrameLimit = maxFrameLimit;
    }

    @Override
    public void lifecycleManager(Http2LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
        super.lifecycleManager(lifecycleManager);
    }

    @Override
    public ChannelFuture writeSettingsAck(ChannelHandlerContext ctx, ChannelPromise promise) {
        ChannelPromise newPromise = handleOutstandingControlFrames(ctx, promise);
        if (newPromise == null) {
            return promise;
        }
        return super.writeSettingsAck(ctx, newPromise);
    }

    @Override
    public ChannelFuture writePing(ChannelHandlerContext ctx, boolean ack, long data, ChannelPromise promise) {
        // Only apply the limit to ping acks.
        if (ack) {
            ChannelPromise newPromise = handleOutstandingControlFrames(ctx, promise);
            if (newPromise == null) {
                return promise;
            }
            return super.writePing(ctx, ack, data, newPromise);
        }
        return super.writePing(ctx, ack, data, promise);
    }

    @Override
    public ChannelFuture writeRstStream(
            ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise) {
        ChannelPromise newPromise = handleOutstandingControlFrames(ctx, promise);
        if (newPromise == null) {
            return promise;
        }
        return super.writeRstStream(ctx, streamId, errorCode, newPromise);
    }

    private ChannelPromise handleOutstandingControlFrames(ChannelHandlerContext ctx, ChannelPromise promise) {
        if (!limitReached) {
            if (outstandingControlFrames == maxFrameLimit) {
                // Let's try to flush once as we may be able to flush some of the control frames.
                ctx.flush();
            }
            if (outstandingControlFrames == maxFrameLimit) {
                limitReached = true;
                Http2Exception exception = Http2Exception.connectionError(Http2Error.ENHANCE_YOUR_CALM,
                        "Maximum number %d of outstanding control frames reached", maxFrameLimit);
                logger.info("Maximum number {} of outstanding control frames reached. Closing channel {}",
                        maxFrameLimit, ctx.channel(), exception);

                // First notify the Http2LifecycleManager and then close the connection.
                lifecycleManager.onError(ctx, true, exception);
                ctx.close();
            }
            outstandingControlFrames++;

            // We did not reach the limit yet, add the listener to decrement the number of outstanding control frames
            // once the promise was completed
            return promise.unvoid().addListener(outstandingControlFramesListener);
        }
        return promise;
    }
}
