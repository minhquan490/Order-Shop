package com.bachlinh.order.web.common.listener;

import io.netty.handler.codec.http2.Http2Frame;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.http.server.listener.AbstractHttpFrameListenerFactory;
import com.bachlinh.order.http.server.listener.HttpFrameListener;
import com.bachlinh.order.http.server.listener.NoOpsStompFrameListener;
import com.bachlinh.order.http.server.listener.StompFrameListener;

public class Netty2FrameListenerFactory extends AbstractHttpFrameListenerFactory<Http2Frame> {

    public Netty2FrameListenerFactory(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected HttpFrameListener<Http2Frame> doCreateHttpFrameListener() {
        return new DefaultHttp2Listener(getRouter(), getFilterChainAdapter());
    }

    @Override
    protected StompFrameListener doCreateStompFrameListener() {
        return new NoOpsStompFrameListener();
    }
}
