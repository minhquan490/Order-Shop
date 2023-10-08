package com.bachlinh.order.web.common.listener;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.server.netty.listener.AbstractHttpFrameListenerFactory;
import com.bachlinh.order.core.server.netty.listener.HttpFrameListener;
import com.bachlinh.order.core.server.netty.listener.NoOpsStompFrameListener;
import com.bachlinh.order.core.server.netty.listener.StompFrameListener;
import io.netty.incubator.codec.http3.Http3Frame;

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
