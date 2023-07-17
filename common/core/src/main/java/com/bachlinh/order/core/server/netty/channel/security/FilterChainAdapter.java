package com.bachlinh.order.core.server.netty.channel.security;

import com.bachlinh.order.core.http.parser.spi.NettyHttpConvention;
import com.bachlinh.order.core.server.netty.channel.adapter.NettyServletRequestAdapter;
import com.bachlinh.order.core.server.netty.channel.adapter.WrappedRequest;
import com.bachlinh.order.core.server.netty.strategy.NettyHandlerContextStrategy;
import com.bachlinh.order.exception.system.server.RequestInvalidException;
import com.bachlinh.order.service.container.DependenciesResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.FilterChainProxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FilterChainAdapter implements FilterChain {
    private final FilterChainProxy proxy;

    public FilterChainAdapter(DependenciesResolver resolver) {
        this.proxy = resolver.resolveDependencies(FilterChainProxy.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        proxy.doFilter(request, response, this);
    }

    public void interceptRequest(FullHttpRequest req, ChannelHandlerContext ctx, HttpServletResponse sharedResponse, NettyHandlerContextStrategy strategy) throws ServletException, IOException {
        var wrappedRequest = new WrappedRequest(req);
        wrappedRequest.setRemoteAddress(ctx.channel().remoteAddress());
        var proxyRequest = NettyServletRequestAdapter.from(wrappedRequest);
        this.doFilter(proxyRequest, sharedResponse);
        if (sharedResponse.isCommitted()) {
            var convention = (NettyHttpConvention) sharedResponse;
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("strategy", strategy);
            attributes.put("convention", convention);
            throw new RequestInvalidException(attributes);
        }
    }
}
