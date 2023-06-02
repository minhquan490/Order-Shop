package com.bachlinh.order.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import com.bachlinh.order.core.tcp.iterceptor.AbstractWebSocketInterceptor;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.security.auth.spi.PrincipalHolder;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.helper.AuthenticationHelper;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.HeaderUtils;

import java.util.Map;

public class SocketAuthorizeInterceptor extends AbstractWebSocketInterceptor {
    private TokenManager tokenManager;
    private CustomerRepository customerRepository;

    public SocketAuthorizeInterceptor(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    @Override
    protected boolean authorizeHandshakeRequest(ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String jwt = servletRequest.getHeader(HeaderUtils.getAuthorizeHeader());
        String refreshToken = servletRequest.getHeader(HeaderUtils.getRefreshHeader());
        Map<String, Object> claims = AuthenticationHelper.parseAuthentication(jwt, refreshToken, tokenManager);
        if (claims.isEmpty()) {
            return false;
        } else {
            Customer customer = customerRepository.getCustomerById((String) claims.get(Customer_.ID), false);
            if (customer == null) {
                return false;
            }
            SecurityContextHolder.getContext().setAuthentication(new PrincipalHolder(customer, null));
            return true;
        }
    }

    @Override
    protected void inject() {
        if (tokenManager == null) {
            tokenManager = getDependenciesResolver().resolveDependencies(TokenManager.class);
        }
        if (customerRepository == null) {
            customerRepository = getDependenciesResolver().resolveDependencies(CustomerRepository.class);
        }
    }
}
