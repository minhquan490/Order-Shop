package com.bachlinh.order.security.filter.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.util.PathMatcher;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.http.UnAuthorizationException;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.filter.AbstractWebFilter;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Filter for determine client secret token before allow user to
 * get resources.
 *
 * @author Hoang Minh Quan
 */
public class ClientSecretFilter extends AbstractWebFilter {
    private final Environment environment;
    private static final String ERROR_MESSAGE = "You need to login to continue";
    private UnAuthorizationHandler handler;
    private TokenManager tokenManager;
    private final Collection<String> excludePaths;
    private final PathMatcher pathMatcher;// Use AntMatcher

    public ClientSecretFilter(DependenciesContainerResolver containerResolver, Collection<String> excludePaths, PathMatcher pathMatcher, String profile) {
        super(containerResolver.getDependenciesResolver());
        this.excludePaths = excludePaths;
        this.environment = Environment.getInstance(profile);
        this.pathMatcher = pathMatcher;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Collection<Cookie> cookies = request.getCookies() == null ? Collections.emptyList() : Arrays.asList(request.getCookies());
        Cookie secretCookie = containsSecretCookie(cookies);
        if (cookies.isEmpty() || secretCookie == null) {
            handler.onAuthenticationFailure(response, new UnAuthorizationException(ERROR_MESSAGE));
            return;
        }
        if (!secretCookie.isHttpOnly() || !secretCookie.getSecure()) {
            handler.onAuthenticationFailure(response, new UnAuthorizationException(ERROR_MESSAGE));
            return;
        }
        String clientSecret = secretCookie.getValue();
        if (clientSecret == null) {
            handler.onAuthenticationFailure(response, new UnAuthorizationException(ERROR_MESSAGE));
            return;
        }
        if (!tokenManager.isWrapped(clientSecret)) {
            handler.onAuthenticationFailure(response, new UnAuthorizationException(ERROR_MESSAGE));
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected void inject() {
        if (handler == null) {
            handler = getDependenciesResolver().resolveDependencies(UnAuthorizationHandler.class);
        }
        if (tokenManager == null) {
            tokenManager = getDependenciesResolver().resolveDependencies(TokenManager.class);
        }
    }

    private Cookie containsSecretCookie(Collection<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(environment.getProperty("server.cookie.name"))) {
                return cookie;
            }
        }
        return null;
    }
}
