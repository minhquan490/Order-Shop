package com.bachlinh.order.security.filter.grpc;

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
import com.bachlinh.order.security.filter.GrpcWebFilter;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.security.helper.AuthenticationHelper;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class AuthenticationFilter extends GrpcWebFilter {
    private TokenManager tokenManager;
    private CustomerRepository customerRepository;
    private UnAuthorizationHandler authenticationFailureHandler;
    private Collection<String> excludePaths;
    private PathMatcher pathMatcher;// Use AntMatcher

    public AuthenticationFilter(DependenciesResolver resolver, String profile) {
        super(resolver, profile);
    }

    @Override
    public AuthenticationFilter setExcludePaths(Collection<String> excludePaths) {
        this.excludePaths = excludePaths;
        return this;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void intercept(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        if (pathMatcher == null) {
            pathMatcher = getDependenciesResolver().resolveDependencies(PathMatcher.class);
        }
    }
}
