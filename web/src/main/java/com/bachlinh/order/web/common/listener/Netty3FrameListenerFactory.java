package com.bachlinh.order.web.common.listener;

import io.netty.incubator.codec.http3.Http3Frame;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.http.server.listener.AbstractHttpFrameListenerFactory;
import com.bachlinh.order.http.server.listener.HttpFrameListener;
import com.bachlinh.order.http.server.listener.NoOpsStompFrameListener;
import com.bachlinh.order.http.server.listener.StompFrameListener;

public class Netty3FrameListenerFactory extends AbstractHttpFrameListenerFactory<Http3Frame> {

    public Netty3FrameListenerFactory(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected HttpFrameListener<Http3Frame> doCreateHttpFrameListener() {
        return new DefaultHttp3Listener(getRouter(), getFilterChainAdapter());
    }

    @Override
    protected StompFrameListener doCreateStompFrameListener() {
        return new NoOpsStompFrameListener();
    }
}
