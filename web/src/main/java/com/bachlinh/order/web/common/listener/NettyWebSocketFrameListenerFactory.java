package com.bachlinh.order.web.common.listener;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.server.netty.channel.stomp.NettyConnectionManager;
import com.bachlinh.order.core.server.netty.listener.AbstractFrameListenerFactory;
import com.bachlinh.order.core.server.netty.listener.HttpFrameListener;
import com.bachlinh.order.core.server.netty.listener.NoOpsHttpListener;
import com.bachlinh.order.core.server.netty.listener.StompFrameListener;
import com.bachlinh.order.core.utils.HeaderUtils;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.security.auth.spi.TokenManager;
import io.netty.handler.codec.http.FullHttpRequest;

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
            nettyConnectionManager = (NettyConnectionManager) getResolver().resolveDependencies("nettyConnectionManager");
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
