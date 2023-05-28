package com.bachlinh.order.security.filter.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PathMatcher;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.security.auth.spi.PrincipalHolder;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.filter.AbstractWebFilter;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.security.helper.AuthenticationHelper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

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
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> claims = AuthenticationHelper.parseAuthentication(request, response, tokenManager);
        if (claims.isEmpty()) {
            authenticationFailureHandler.onAuthenticationFailure(response, new UnAuthorizationException("Missing token, login is required"));
            return;
        }
        Customer customer = customerRepository.getCustomerById((String) claims.get("customerId"), true);
        if (customer == null) {
            authenticationFailureHandler.onAuthenticationFailure(response, new UnAuthorizationException("Invalid credential"));
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(new PrincipalHolder(customer, AuthenticationHelper.findClientSecret(request)));
        filterChain.doFilter(request, response);
    }

    @Override
    protected void inject() {
        if (tokenManager == null) {
            tokenManager = getDependenciesResolver().resolveDependencies(TokenManager.class);
        }
        if (customerRepository == null) {
            customerRepository = getDependenciesResolver().resolveDependencies(CustomerRepository.class);
        }
        if (authenticationFailureHandler == null) {
            authenticationFailureHandler = getDependenciesResolver().resolveDependencies(UnAuthorizationHandler.class);
        }
    }
}
