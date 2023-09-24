package com.bachlinh.order.web.common.listener;

import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.core.server.netty.listener.AbstractHttpFrameListenerFactory;
import com.bachlinh.order.core.server.netty.listener.HttpFrameListener;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.common.servlet.ServletRouter;
import io.netty.handler.codec.http2.Http2Frame;

public class NettyHttp2FrameListenerFactory extends AbstractHttpFrameListenerFactory<Http2Frame> {

    private final ServletRouter servletRouter;
    private final FilterChainAdapter filterChainAdapter;

    public NettyHttp2FrameListenerFactory(DependenciesResolver resolver) {
        super(resolver);
        this.servletRouter = getResolver().resolveDependencies(ServletRouter.class);
        this.filterChainAdapter = getResolver().resolveDependencies(FilterChainAdapter.class);
    }

    @Override
    public HttpFrameListener<Http2Frame> createFrameListener() {
        return new DefaultHttp2Listener(servletRouter, filterChainAdapter);
    }
}
