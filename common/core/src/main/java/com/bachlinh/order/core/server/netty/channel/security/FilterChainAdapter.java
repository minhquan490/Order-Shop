package com.bachlinh.order.core.server.netty.channel.security;

import com.bachlinh.order.core.function.ServletCallback;
import com.bachlinh.order.core.server.netty.channel.adapter.NettyServletRequestAdapter;
import com.bachlinh.order.core.server.netty.channel.adapter.WrappedRequest;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.container.DependenciesResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.FilterChainProxy;

import java.io.IOException;

public class FilterChainAdapter implements FilterChain {
    private final FilterChainProxy proxy;
    private ServletCallback callback;

    public FilterChainAdapter(DependenciesResolver resolver) {
        this.proxy = resolver.resolveDependencies(FilterChainProxy.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (callback == null) {
            throw new CriticalException("Call interceptRequest before call this method");
        }
        proxy.doFilter(request, response, (request1, response1) -> callback.call((HttpServletRequest) request1, (HttpServletResponse) response1));
    }

    public void interceptRequest(FullHttpRequest req, ChannelHandlerContext ctx, HttpServletResponse sharedResponse, ServletCallback callback) throws ServletException, IOException {
        var wrappedRequest = new WrappedRequest(req);
        try {
            this.callback = callback;
            wrappedRequest.setRemoteAddress(ctx.channel().remoteAddress());
            var proxyRequest = NettyServletRequestAdapter.from(wrappedRequest);
            this.doFilter(proxyRequest, sharedResponse);
        } finally {
            wrappedRequest.release();
        }
    }
}
