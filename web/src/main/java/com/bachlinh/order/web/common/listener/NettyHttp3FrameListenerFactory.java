package com.bachlinh.order.web.common.listener;

import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.listener.AbstractHttpFrameListenerFactory;
import com.bachlinh.order.core.server.netty.listener.HttpFrameListener;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.web.common.servlet.ServletRouter;
import io.netty.incubator.codec.http3.Http3Frame;

public class NettyHttp3FrameListenerFactory extends AbstractHttpFrameListenerFactory<Http3Frame> {

    private final ServletRouter servletRouter;
    private final FilterChainAdapter filterChainAdapter;

    public NettyHttp3FrameListenerFactory(DependenciesResolver resolver) {
        super(resolver);
        this.servletRouter = getResolver().resolveDependencies(ServletRouter.class);
        this.filterChainAdapter = getResolver().resolveDependencies(FilterChainAdapter.class);
    }

    @Override
    public HttpFrameListener<Http3Frame> createFrameListener() {
        return new DefaultHttp3Listener(servletRouter, filterChainAdapter);
    }
}
