package com.bachlinh.order.security.filter.grpc;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.exception.http.AccessDeniedException;
import com.bachlinh.order.security.auth.spi.CsrfTokenMatcher;
import com.bachlinh.order.security.auth.spi.InMemoryCsrfTokenCaching;
import com.bachlinh.order.security.filter.GrpcWebFilter;
import com.bachlinh.order.security.handler.AccessDeniedHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DeferredCsrfToken;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.Collection;

public class CsrfFilter extends GrpcWebFilter {
    private CsrfTokenRepository csrfTokenRepository;
    private CsrfTokenMatcher csrfTokenMatcher;
    private InMemoryCsrfTokenCaching inMemoryCsrfTokenCaching;
    private Collection<String> excludePaths;
    private PathMatcher pathMatcher;
    private AccessDeniedHandler accessDeniedHandler;

    public CsrfFilter(DependenciesResolver dependenciesResolver, String profile) {
        super(dependenciesResolver, profile);
    }

    @Override
    public CsrfFilter setExcludePaths(Collection<String> excludePaths) {
        this.excludePaths = excludePaths;
        return this;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void intercept(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DeferredCsrfToken deferredCsrfToken = csrfTokenRepository.loadDeferredToken(request, response);
        CsrfToken clientCsrfToken = csrfTokenRepository.loadToken(request);
        boolean matched = csrfTokenMatcher.match(clientCsrfToken.getToken(), deferredCsrfToken.get().getToken());
        if (!matched) {
            CsrfToken generatedToken = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(generatedToken, request, response);
            accessDeniedHandler.handle(response, new AccessDeniedException("Invalid csrf token, access resource is denied"));
        } else {
            inMemoryCsrfTokenCaching.releaseToken(request);
            CsrfToken generatedToken = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(generatedToken, request, response);
        }
    }

    @Override
    protected void inject() {
        if (pathMatcher == null) {
            pathMatcher = resolveDependencies(PathMatcher.class);
        }
        if (csrfTokenRepository == null) {
            csrfTokenRepository = resolveRepository(CsrfTokenRepository.class);
        }
        if (csrfTokenMatcher == null) {
            csrfTokenMatcher = resolveDependencies(CsrfTokenMatcher.class);
        }
        if (accessDeniedHandler == null) {
            accessDeniedHandler = resolveDependencies(AccessDeniedHandler.class);
        }
        if (inMemoryCsrfTokenCaching == null) {
            inMemoryCsrfTokenCaching = resolveDependencies(InMemoryCsrfTokenCaching.class);
        }
    }
}
