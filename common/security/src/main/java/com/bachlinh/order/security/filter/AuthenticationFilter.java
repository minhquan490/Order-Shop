package com.bachlinh.order.security.filter;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.security.auth.spi.PrincipalHolder;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.security.helper.AuthenticationHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PathMatcher;

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
    private final TokenManager tokenManager;
    private final CustomerRepository customerRepository;
    private final UnAuthorizationHandler authenticationFailureHandler;
    private final Collection<String> excludePaths;
    private final PathMatcher pathMatcher;// Use AntMatcher

    public AuthenticationFilter(ApplicationContext applicationContext, Collection<String> excludePaths, PathMatcher pathMatcher) {
        super(applicationContext);
        this.tokenManager = applicationContext.getBean(TokenManager.class);
        this.customerRepository = applicationContext.getBean(CustomerRepository.class);
        this.authenticationFailureHandler = applicationContext.getBean(UnAuthorizationHandler.class);
        this.excludePaths = excludePaths;
        this.pathMatcher = pathMatcher;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
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
        SecurityContextHolder.getContext().setAuthentication(new PrincipalHolder(customer));
        filterChain.doFilter(request, response);
    }
}
