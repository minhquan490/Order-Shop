package com.bachlinh.order.web.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.exception.http.UnAuthorizationException;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.security.auth.spi.PrincipalHolder;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.filter.AbstractWebFilter;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.security.helper.AuthenticationHelper;
import com.bachlinh.order.web.repository.spi.CustomerRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PathMatcher;

/**
 * Filter use for authentication user with jwt and refresh token
 * before processing a request.
 *
 * @author Hoang Minh Quan.
 */
public class AuthenticationFilter extends AbstractWebFilter {
    private TokenManager tokenManager;
    private CustomerRepository customerRepository;
    private UnAuthorizationHandler authenticationFailureHandler;
    private final Collection<String> excludePaths;
    private final PathMatcher pathMatcher;// Use AntMatcher

    public AuthenticationFilter(DependenciesContainerResolver containerResolver, Collection<String> excludePaths, PathMatcher pathMatcher) {
        super(containerResolver.getDependenciesResolver());
        this.excludePaths = excludePaths;
        this.pathMatcher = pathMatcher;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> claims = AuthenticationHelper.parseAuthentication(request, response, tokenManager);
        if (claims.isEmpty()) {
            authenticationFailureHandler.onAuthenticationFailure(response, new UnAuthorizationException("Missing token, login is required", request.getRequestURI()));
            return;
        }
        Customer customer = customerRepository.getCustomerForAuthentication((String) claims.get(Customer_.ID));
        if (customer == null) {
            authenticationFailureHandler.onAuthenticationFailure(response, new UnAuthorizationException("Invalid credential", request.getRequestURI()));
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(new PrincipalHolder(customer));
        filterChain.doFilter(request, response);
    }

    @Override
    protected void inject() {
        if (tokenManager == null) {
            tokenManager = resolveDependencies(TokenManager.class);
        }
        if (customerRepository == null) {
            customerRepository = resolveRepository(CustomerRepository.class);
        }
        if (authenticationFailureHandler == null) {
            authenticationFailureHandler = resolveDependencies(UnAuthorizationHandler.class);
        }
    }
}
