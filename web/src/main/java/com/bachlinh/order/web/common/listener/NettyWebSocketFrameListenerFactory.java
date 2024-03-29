package com.bachlinh.order.web.common.listener;

import io.netty.handler.codec.http.FullHttpRequest;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.http.server.channel.stomp.NettyConnectionManager;
import com.bachlinh.order.http.server.listener.AbstractFrameListenerFactory;
import com.bachlinh.order.http.server.listener.HttpFrameListener;
import com.bachlinh.order.http.server.listener.NoOpsHttpListener;
import com.bachlinh.order.http.server.listener.StompFrameListener;
import com.bachlinh.order.core.utils.HeaderUtils;
import com.bachlinh.order.repository.RepositoryManager;
import com.bachlinh.order.web.repository.spi.CustomerRepository;
import com.bachlinh.order.security.auth.spi.TokenManager;

public class NettyWebSocketFrameListenerFactory extends AbstractFrameListenerFactory<FullHttpRequest> {

    private NettyConnectionManager nettyConnectionManager;
    private TokenManager tokenManager;
    private CustomerRepository customerRepository;

    public NettyWebSocketFrameListenerFactory(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected HttpFrameListener<FullHttpRequest> doCreateHttpFrameListener() {
        return new NoOpsHttpListener();
    }

    @Override
    protected StompFrameListener doCreateStompFrameListener() {
        return new NettyWebSocketFrameListener(customerRepository, nettyConnectionManager, tokenManager, HeaderUtils.getAuthorizeHeader(), HeaderUtils.getRefreshHeader());
    }

    @Override
    protected void inject() {
        if (nettyConnectionManager == null) {
            nettyConnectionManager = getResolver().resolveDependencies("nettyConnectionManager", NettyConnectionManager.class);
        }
        if (tokenManager == null) {
            tokenManager = getResolver().resolveDependencies(TokenManager.class);
        }
        if (customerRepository == null) {
            RepositoryManager repositoryManager = getResolver().resolveDependencies(RepositoryManager.class);
            customerRepository = repositoryManager.getRepository(CustomerRepository.class);
        }
    }
}
