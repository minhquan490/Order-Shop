package com.bachlinh.order.http.server.shaded;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.DefaultHttp2PingFrame;
import io.netty.handler.codec.http2.DefaultHttp2PriorityFrame;
import io.netty.handler.codec.http2.DefaultHttp2ResetFrame;
import io.netty.handler.codec.http2.DefaultHttp2SettingsFrame;
import io.netty.handler.codec.http2.DefaultHttp2UnknownFrame;
import io.netty.handler.codec.http2.DefaultHttp2WindowUpdateFrame;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionAdapter;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2FrameStreamException;
import io.netty.handler.codec.http2.Http2FrameStreamVisitor;
import io.netty.handler.codec.http2.Http2GoAwayFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2NoMoreStreamIdsException;
import io.netty.handler.codec.http2.Http2PingFrame;
import io.netty.handler.codec.http2.Http2PriorityFrame;
import io.netty.handler.codec.http2.Http2PushPromiseFrame;
import io.netty.handler.codec.http2.Http2RemoteFlowController;
import io.netty.handler.codec.http2.Http2ResetFrame;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2SettingsAckFrame;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.handler.codec.http2.Http2UnknownFrame;
import io.netty.handler.codec.http2.Http2WindowUpdateFrame;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.handler.codec.http2.StreamBufferingEncoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.UnstableApi;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import com.bachlinh.order.http.server.channel.http2.ProxiedHttp2Connection;

/**
 * Shaded api for custom, {@link Http2ConnectionHandler#connection()} will return {@link ProxiedHttp2Connection}
 * for tracking client connection.
 */
@UnstableApi
public class Http2FrameCodec extends Http2ConnectionHandler {

    private static final InternalLogger LOG = InternalLoggerFactory.getInstance(io.netty.handler.codec.http2.Http2FrameCodec.class);

    protected final Http2Connection.PropertyKey streamKey;
    private final Http2Connection.PropertyKey upgradeKey;

    private final Integer initialFlowControlWindowSize;

    ChannelHandlerContext ctx;

    /**
     * Number of buffered streams if the {@link StreamBufferingEncoder} is used.
     **/
    private int numBufferedStreams;
    private final IntObjectMap<DefaultHttp2FrameStream> frameStreamToInitializeMap =
            new IntObjectHashMap<>(8);

    Http2FrameCodec(Http2ConnectionEncoder encoder, Http2ConnectionDecoder decoder, Http2Settings initialSettings,
                    boolean decoupleCloseAndGoAway, boolean flushPreface) {
        super(decoder, encoder, initialSettings, decoupleCloseAndGoAway, flushPreface);

        decoder.frameListener(new FrameListener());
        connection().addListener(new ConnectionListener());
        connection().remote().flowController().listener(new Http2RemoteFlowControllerListener());
        streamKey = connection().newKey();
        upgradeKey = connection().newKey();
        initialFlowControlWindowSize = initialSettings.initialWindowSize();
    }

    /**
     * Creates a new outbound/local stream.
     */
    DefaultHttp2FrameStream newStream() {
        return new DefaultHttp2FrameStream();
    }

    /**
     * Iterates over all active HTTP/2 streams.
     *
     * <p>This method must not be called outside of the event loop.
     */
    final void forEachActiveStream(final Http2FrameStreamVisitor streamVisitor) throws Http2Exception {
        assert ctx.executor().inEventLoop();
        if (connection().numActiveStreams() > 0) {
            connection().forEachActiveStream(stream -> {
                try {
                    return streamVisitor.visit(stream.getProperty(streamKey));
                } catch (Throwable cause) {
                    onError(ctx, false, cause);
                    return false;
                }
            });
        }
    }

    /**
     * Retrieve the number of streams currently in the process of being initialized.
     * <p>
     * This is package-private for testing only.
     */
    int numInitializingStreams() {
        return frameStreamToInitializeMap.size();
    }

    @Override
    public final void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.handlerAdded(ctx);
        handlerAdded0(ctx);
        // Must be after Http2ConnectionHandler does its initialization in handlerAdded above.
        // The server will not send a connection preface so we are good to send a window update.
        Http2Connection connection = connection();
        if (connection.isServer()) {
            tryExpandConnectionFlowControlWindow(connection);
        }
    }

    private void tryExpandConnectionFlowControlWindow(Http2Connection connection) throws Http2Exception {
        if (initialFlowControlWindowSize != null) {
            // The window size in the settings explicitly excludes the connection window. So we manually manipulate the
            // connection window to accommodate more concurrent data per connection.
            Http2Stream connectionStream = connection.connectionStream();
            Http2LocalFlowController localFlowController = connection.local().flowController();
            final int delta = initialFlowControlWindowSize - localFlowController.initialWindowSize(connectionStream);
            // Only increase the connection window, don't decrease it.
            if (delta > 0) {
                // Double the delta just so a single stream can't exhaust the connection window.
                localFlowController.incrementWindowSize(connectionStream, Math.max(delta << 1, delta));
                flush(ctx);
            }
        }
    }

    void handlerAdded0(@SuppressWarnings("unsed") ChannelHandlerContext ctx) throws Exception {
        // sub-class can override this for extra steps that needs to be done when the handler is added.
    }

    /**
     * Handles the cleartext HTTP upgrade event. If an upgrade occurred, sends a simple response via
     * HTTP/2 on stream 1 (the stream specifically reserved for cleartext HTTP upgrade).
     */
    @Override
    public final void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt == Http2ConnectionPrefaceAndSettingsFrameWrittenEvent.INSTANCE) {
            // The user event implies that we are on the client.
            tryExpandConnectionFlowControlWindow(connection());

            // We schedule this on the EventExecutor to allow to have any extra handlers added to the pipeline
            // before we pass the event to the next handler. This is needed as the event may be called from within
            // handlerAdded(...) which will be run before other handlers will be added to the pipeline.
            ctx.executor().execute(() -> ctx.fireUserEventTriggered(evt));
        } else if (evt instanceof HttpServerUpgradeHandler.UpgradeEvent upgrade) {
            try {
                onUpgradeEvent(ctx, upgrade.retain());
                Http2Stream stream = connection().stream(Http2CodecUtil.HTTP_UPGRADE_STREAM_ID);
                if (stream.getProperty(streamKey) == null) {
                    // TODO: improve handler/stream lifecycle so that stream isn't active before handler added.
                    // The stream was already made active, but ctx may have been null so it wasn't initialized.
                    // https://github.com/netty/netty/issues/4942
                    onStreamActive0(stream);
                }
                upgrade.upgradeRequest().headers().setInt(
                        HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), Http2CodecUtil.HTTP_UPGRADE_STREAM_ID);
                stream.setProperty(upgradeKey, true);
                InboundHttpToHttp2Adapter.handle(
                        ctx, connection(), decoder().frameListener(), upgrade.upgradeRequest().retain());
            } finally {
                upgrade.release();
            }
        } else {
            onUserEventTriggered(ctx, evt);
            ctx.fireUserEventTriggered(evt);
        }
    }

    void onUserEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        // noop
    }

    /**
     * Processes all {@link Http2Frame}s. {@link Http2StreamFrame}s may only originate in child
     * streams.
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (msg instanceof Http2DataFrame dataFrame) {
            encoder().writeData(ctx, dataFrame.stream().id(), dataFrame.content(),
                    dataFrame.padding(), dataFrame.isEndStream(), promise);
        } else if (msg instanceof Http2HeadersFrame headersFrame) {
            writeHeadersFrame(ctx, headersFrame, promise);
        } else if (msg instanceof Http2WindowUpdateFrame frame) {
            Http2FrameStream frameStream = frame.stream();
            // It is legit to send a WINDOW_UPDATE frame for the connection stream. The parent channel doesn't attempt
            // to set the Http2FrameStream so we assume if it is null the WINDOW_UPDATE is for the connection stream.
            try {
                if (frameStream == null) {
                    increaseInitialConnectionWindow(frame.windowSizeIncrement());
                } else {
                    consumeBytes(frameStream.id(), frame.windowSizeIncrement());
                }
                promise.setSuccess();
            } catch (Throwable t) {
                promise.setFailure(t);
            }
        } else if (msg instanceof Http2ResetFrame rstFrame) {
            int id = rstFrame.stream().id();
            // Only ever send a reset frame if stream may have existed before as otherwise we may send a RST on a
            // stream in an invalid state and cause a connection error.
            if (connection().streamMayHaveExisted(id)) {
                encoder().writeRstStream(ctx, rstFrame.stream().id(), rstFrame.errorCode(), promise);
            } else {
                ReferenceCountUtil.release(rstFrame);
                promise.setFailure(Http2Exception.streamError(
                        rstFrame.stream().id(), Http2Error.PROTOCOL_ERROR, "Stream never existed"));
            }
        } else if (msg instanceof Http2PingFrame frame) {
            encoder().writePing(ctx, frame.ack(), frame.content(), promise);
        } else if (msg instanceof Http2SettingsFrame settingsFrame) {
            encoder().writeSettings(ctx, settingsFrame.settings(), promise);
        } else if (msg instanceof Http2SettingsAckFrame) {
            // In the event of manual SETTINGS ACK, it is assumed the encoder will apply the earliest received but not
            // yet ACKed settings.
            encoder().writeSettingsAck(ctx, promise);
        } else if (msg instanceof Http2GoAwayFrame goAwayFrame) {
            writeGoAwayFrame(ctx, goAwayFrame, promise);
        } else if (msg instanceof Http2PushPromiseFrame pushPromiseFrame) {
            writePushPromise(ctx, pushPromiseFrame, promise);
        } else if (msg instanceof Http2PriorityFrame priorityFrame) {
            encoder().writePriority(ctx, priorityFrame.stream().id(), priorityFrame.streamDependency(),
                    priorityFrame.weight(), priorityFrame.exclusive(), promise);
        } else if (msg instanceof Http2UnknownFrame unknownFrame) {
            encoder().writeFrame(ctx, unknownFrame.frameType(), unknownFrame.stream().id(),
                    unknownFrame.flags(), unknownFrame.content(), promise);
        } else if (!(msg instanceof Http2Frame)) {
            ctx.write(msg, promise);
        } else {
            ReferenceCountUtil.release(msg);
            throw new UnsupportedMessageTypeException(msg);
        }
    }

    private void increaseInitialConnectionWindow(int deltaBytes) throws Http2Exception {
        // The LocalFlowController is responsible for detecting over/under flow.
        connection().local().flowController().incrementWindowSize(connection().connectionStream(), deltaBytes);
    }

    final boolean consumeBytes(int streamId, int bytes) throws Http2Exception {
        Http2Stream stream = connection().stream(streamId);
        // Upgraded requests are ineligible for stream control. We add the null check
        // in case the stream has been deregistered.
        if (stream != null && streamId == Http2CodecUtil.HTTP_UPGRADE_STREAM_ID) {
            Boolean upgraded = stream.getProperty(upgradeKey);
            if (Boolean.TRUE.equals(upgraded)) {
                return false;
            }
        }

        return connection().local().flowController().consumeBytes(stream, bytes);
    }

    private void writeGoAwayFrame(ChannelHandlerContext ctx, Http2GoAwayFrame frame, ChannelPromise promise) {
        if (frame.lastStreamId() > -1) {
            frame.release();
            throw new IllegalArgumentException("Last stream id must not be set on GOAWAY frame");
        }

        int lastStreamCreated = connection().remote().lastStreamCreated();
        long lastStreamId = lastStreamCreated + ((long) frame.extraStreamIds()) * 2;
        // Check if the computation overflowed.
        if (lastStreamId > Integer.MAX_VALUE) {
            lastStreamId = Integer.MAX_VALUE;
        }
        goAway(ctx, (int) lastStreamId, frame.errorCode(), frame.content(), promise);
    }

    private void writeHeadersFrame(final ChannelHandlerContext ctx, Http2HeadersFrame headersFrame,
                                   final ChannelPromise promise) {

        if (Http2CodecUtil.isStreamIdValid(headersFrame.stream().id())) {
            encoder().writeHeaders(ctx, headersFrame.stream().id(), headersFrame.headers(), headersFrame.padding(),
                    headersFrame.isEndStream(), promise);
        } else if (initializeNewStream(ctx, (DefaultHttp2FrameStream) headersFrame.stream(), promise)) {
            final int streamId = headersFrame.stream().id();

            encoder().writeHeaders(ctx, streamId, headersFrame.headers(), headersFrame.padding(),
                    headersFrame.isEndStream(), promise);

            if (!promise.isDone()) {
                numBufferedStreams++;
                // Clean up the stream being initialized if writing the headers fails and also
                // decrement the number of buffered streams.
                promise.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) {
                        numBufferedStreams--;
                        handleHeaderFuture(channelFuture, streamId);
                    }
                });
            } else {
                handleHeaderFuture(promise, streamId);
            }
        }
    }

    private void writePushPromise(final ChannelHandlerContext ctx, Http2PushPromiseFrame pushPromiseFrame,
                                  final ChannelPromise promise) {
        if (Http2CodecUtil.isStreamIdValid(pushPromiseFrame.pushStream().id())) {
            encoder().writePushPromise(ctx, pushPromiseFrame.stream().id(), pushPromiseFrame.pushStream().id(),
                    pushPromiseFrame.http2Headers(), pushPromiseFrame.padding(), promise);
        } else if (initializeNewStream(ctx, (DefaultHttp2FrameStream) pushPromiseFrame.pushStream(), promise)) {
            final int streamId = pushPromiseFrame.stream().id();
            encoder().writePushPromise(ctx, streamId, pushPromiseFrame.pushStream().id(),
                    pushPromiseFrame.http2Headers(), pushPromiseFrame.padding(), promise);

            if (promise.isDone()) {
                handleHeaderFuture(promise, streamId);
            } else {
                numBufferedStreams++;
                // Clean up the stream being initialized if writing the headers fails and also
                // decrement the number of buffered streams.
                promise.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) {
                        numBufferedStreams--;
                        handleHeaderFuture(channelFuture, streamId);
                    }
                });
            }
        }
    }

    private boolean initializeNewStream(ChannelHandlerContext ctx, DefaultHttp2FrameStream http2FrameStream,
                                        ChannelPromise promise) {
        final Http2Connection connection = connection();
        final int streamId = connection.local().incrementAndGetNextStreamId();
        if (streamId < 0) {
            promise.setFailure(new Http2NoMoreStreamIdsException());

            // Simulate a GOAWAY being received due to stream exhaustion on this connection. We use the maximum
            // valid stream ID for the current peer.
            onHttp2Frame(ctx, new DefaultHttp2GoAwayFrame(connection.isServer() ? Integer.MAX_VALUE :
                    Integer.MAX_VALUE - 1, Http2Error.NO_ERROR.code(),
                    ByteBufUtil.writeAscii(ctx.alloc(), "Stream IDs exhausted on local stream creation")));

            return false;
        }
        http2FrameStream.id = streamId;

        // Use a Map to store all pending streams as we may have multiple. This is needed as if we would store the
        // stream in a field directly we may override the stored field before onStreamAdded(...) was called
        // and so not correctly set the property for the buffered stream.
        //
        // See https://github.com/netty/netty/issues/8692
        Object old = frameStreamToInitializeMap.put(streamId, http2FrameStream);

        // We should not re-use ids.
        assert old == null;
        return true;
    }

    private void handleHeaderFuture(ChannelFuture channelFuture, int streamId) {
        if (!channelFuture.isSuccess()) {
            frameStreamToInitializeMap.remove(streamId);
        }
    }

    private void onStreamActive0(Http2Stream stream) {
        if (stream.id() != Http2CodecUtil.HTTP_UPGRADE_STREAM_ID &&
                connection().local().isValidStreamId(stream.id())) {
            return;
        }

        DefaultHttp2FrameStream stream2 = newStream().setStreamAndProperty(streamKey, stream);
        onHttp2StreamStateChanged(ctx, stream2);
    }

    private final class ConnectionListener extends Http2ConnectionAdapter {
        @Override
        public void onStreamAdded(Http2Stream stream) {
            DefaultHttp2FrameStream frameStream = frameStreamToInitializeMap.remove(stream.id());

            if (frameStream != null) {
                frameStream.setStreamAndProperty(streamKey, stream);
            }
        }

        @Override
        public void onStreamActive(Http2Stream stream) {
            onStreamActive0(stream);
        }

        @Override
        public void onStreamClosed(Http2Stream stream) {
            onHttp2StreamStateChanged0(stream);
        }

        @Override
        public void onStreamHalfClosed(Http2Stream stream) {
            onHttp2StreamStateChanged0(stream);
        }

        private void onHttp2StreamStateChanged0(Http2Stream stream) {
            DefaultHttp2FrameStream stream2 = stream.getProperty(streamKey);
            if (stream2 != null) {
                onHttp2StreamStateChanged(ctx, stream2);
            }
        }
    }

    @Override
    protected void onConnectionError(
            ChannelHandlerContext ctx, boolean outbound, Throwable cause, Http2Exception http2Ex) {
        if (!outbound) {
            // allow the user to handle it first in the pipeline, and then automatically clean up.
            // If this is not desired behavior the user can override this method.
            //
            // We only forward non outbound errors as outbound errors will already be reflected by failing the promise.
            ctx.fireExceptionCaught(cause);
        }
        super.onConnectionError(ctx, outbound, cause, http2Ex);
    }

    /**
     * Exceptions for unknown streams, that is streams that have no {@link Http2FrameStream} object attached
     * are simply logged and replied to by sending a RST_STREAM frame.
     */
    @Override
    protected final void onStreamError(ChannelHandlerContext ctx, boolean outbound, Throwable cause,
                                       Http2Exception.StreamException streamException) {
        int streamId = streamException.streamId();
        Http2Stream connectionStream = connection().stream(streamId);
        if (connectionStream == null) {
            onHttp2UnknownStreamError(ctx, cause, streamException);
            // Write a RST_STREAM
            super.onStreamError(ctx, outbound, cause, streamException);
            return;
        }

        Http2FrameStream stream = connectionStream.getProperty(streamKey);
        if (stream == null) {
            LOG.warn("Stream exception thrown without stream object attached.", cause);
            // Write a RST_STREAM
            super.onStreamError(ctx, outbound, cause, streamException);
            return;
        }

        if (!outbound) {
            // We only forward non outbound errors as outbound errors will already be reflected by failing the promise.
            onHttp2FrameStreamException(ctx, new Http2FrameStreamException(stream, streamException.error(), cause));
        }
    }

    private static void onHttp2UnknownStreamError(@SuppressWarnings("unused") ChannelHandlerContext ctx,
                                                  Throwable cause, Http2Exception.StreamException streamException) {
        // We log here for debugging purposes. This exception will be propagated to the upper layers through other ways:
        // - fireExceptionCaught
        // - fireUserEventTriggered(Http2ResetFrame), see Http2MultiplexHandler#channelRead(...)
        // - by failing write promise
        // Receiver of the error is responsible for correct handling of this exception.
        LOG.log(InternalLogLevel.DEBUG, "Stream exception thrown for unknown stream {}.", streamException.streamId(), cause);
    }

    @Override
    protected final boolean isGracefulShutdownComplete() {
        return super.isGracefulShutdownComplete() && numBufferedStreams == 0;
    }

    private final class FrameListener implements Http2FrameListener {

        @Override
        public void onUnknownFrame(
                ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) {
            if (streamId == 0) {
                // Ignore unknown frames on connection stream, for example: HTTP/2 GREASE testing
                return;
            }
            onHttp2Frame(ctx, new DefaultHttp2UnknownFrame(frameType, flags, payload)
                    .stream(requireStream(streamId)).retain());
        }

        @Override
        public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) {
            onHttp2Frame(ctx, new DefaultHttp2SettingsFrame(settings));
        }

        @Override
        public void onPingRead(ChannelHandlerContext ctx, long data) {
            onHttp2Frame(ctx, new DefaultHttp2PingFrame(data, false));
        }

        @Override
        public void onPingAckRead(ChannelHandlerContext ctx, long data) {
            onHttp2Frame(ctx, new DefaultHttp2PingFrame(data, true));
        }

        @Override
        public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) {
            onHttp2Frame(ctx, new DefaultHttp2ResetFrame(errorCode).stream(requireStream(streamId)));
        }

        @Override
        public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement) {
            if (streamId == 0) {
                // Ignore connection window updates.
                return;
            }
            onHttp2Frame(ctx, new DefaultHttp2WindowUpdateFrame(windowSizeIncrement).stream(requireStream(streamId)));
        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId,
                                  Http2Headers headers, int streamDependency, short weight, boolean
                                          exclusive, int padding, boolean endStream) {
            onHeadersRead(ctx, streamId, headers, padding, endStream);
        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers,
                                  int padding, boolean endOfStream) {
            onHttp2Frame(ctx, new DefaultHttp2HeadersFrame(headers, endOfStream, padding)
                    .stream(requireStream(streamId)));
        }

        @Override
        public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding,
                              boolean endOfStream) {
            onHttp2Frame(ctx, new DefaultHttp2DataFrame(data, endOfStream, padding)
                    .stream(requireStream(streamId)).retain());
            // We return the bytes in consumeBytes() once the stream channel consumed the bytes.
            return 0;
        }

        @Override
        public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) {
            onHttp2Frame(ctx, new DefaultHttp2GoAwayFrame(lastStreamId, errorCode, debugData).retain());
        }

        @Override
        public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency,
                                   short weight, boolean exclusive) {

            Http2Stream stream = connection().stream(streamId);
            if (stream == null) {
                // The stream was not opened yet, let's just ignore this for now.
                return;
            }
            onHttp2Frame(ctx, new DefaultHttp2PriorityFrame(streamDependency, weight, exclusive)
                    .stream(requireStream(streamId)));
        }

        @Override
        public void onSettingsAckRead(ChannelHandlerContext ctx) {
            onHttp2Frame(ctx, Http2SettingsAckFrame.INSTANCE);
        }

        @Override
        public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId,
                                      Http2Headers headers, int padding) {
            onHttp2Frame(ctx, new DefaultHttp2PushPromiseFrame(headers, padding, promisedStreamId)
                    .pushStream(new DefaultHttp2FrameStream()
                            .setStreamAndProperty(streamKey, connection().stream(promisedStreamId)))
                    .stream(requireStream(streamId)));
        }

        private Http2FrameStream requireStream(int streamId) {
            Http2FrameStream stream = connection().stream(streamId).getProperty(streamKey);
            if (stream == null) {
                String message = StringTemplate.STR. "Stream object required for identifier: \{ streamId }" ;
                throw new IllegalStateException(message);
            }
            return stream;
        }
    }

    private void onUpgradeEvent(ChannelHandlerContext ctx, HttpServerUpgradeHandler.UpgradeEvent evt) {
        ctx.fireUserEventTriggered(evt);
    }

    private void onHttp2StreamWritabilityChanged(ChannelHandlerContext ctx, DefaultHttp2FrameStream stream,
                                                 @SuppressWarnings("unused") boolean writable) {
        ctx.fireUserEventTriggered(stream.writabilityChanged);
    }

    void onHttp2StreamStateChanged(ChannelHandlerContext ctx, DefaultHttp2FrameStream stream) {
        ctx.fireUserEventTriggered(stream.stateChanged);
    }

    void onHttp2Frame(ChannelHandlerContext ctx, Http2Frame frame) {
        ctx.fireChannelRead(frame);
    }

    void onHttp2FrameStreamException(ChannelHandlerContext ctx, Http2FrameStreamException cause) {
        ctx.fireExceptionCaught(cause);
    }

    private final class Http2RemoteFlowControllerListener implements Http2RemoteFlowController.Listener {
        @Override
        public void writabilityChanged(Http2Stream stream) {
            DefaultHttp2FrameStream frameStream = stream.getProperty(streamKey);
            if (frameStream == null) {
                return;
            }
            onHttp2StreamWritabilityChanged(
                    ctx, frameStream, connection().remote().flowController().isWritable(stream));
        }
    }

    /**
     * {@link Http2FrameStream} implementation.
     */
    // TODO(buchgr): Merge Http2FrameStream and Http2Stream.
    static class DefaultHttp2FrameStream implements Http2FrameStream {

        private volatile int id = -1;
        private volatile Http2Stream stream;

        final Http2FrameStreamEvent stateChanged = Http2FrameStreamEvent.stateChanged(this);
        final Http2FrameStreamEvent writabilityChanged = Http2FrameStreamEvent.writabilityChanged(this);

        Channel attachment;

        DefaultHttp2FrameStream setStreamAndProperty(Http2Connection.PropertyKey streamKey, Http2Stream stream) {
            assert id == -1 || stream.id() == id;
            this.stream = stream;
            stream.setProperty(streamKey, this);
            return this;
        }

        @Override
        public int id() {
            Http2Stream stream = this.stream;
            return stream == null ? id : stream.id();
        }

        @Override
        public Http2Stream.State state() {
            Http2Stream stream = this.stream;
            return stream == null ? Http2Stream.State.IDLE : stream.state();
        }

        @Override
        public String toString() {
            return String.valueOf(id());
        }
    }
}
