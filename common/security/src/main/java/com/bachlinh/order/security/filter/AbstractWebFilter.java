package com.bachlinh.order.security.filter;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.repository.RepositoryManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Base filter for all filters used in this project. If you want to filter
 * follow flow backend to frontend, please confirm to tech lead first.
 *
 * @author Hoang Minh Quan
 */
public abstract class AbstractWebFilter extends OncePerRequestFilter {
    private final DependenciesResolver dependenciesResolver;
    private final RepositoryManager repositoryManager;

    protected AbstractWebFilter(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
        this.repositoryManager = resolveDependencies(RepositoryManager.class);
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected <T> T resolveDependencies(Class<T> dependenciesType) {
        return getDependenciesResolver().resolveDependencies(dependenciesType);
    }

    protected <T> T resolveRepository(Class<T> repositoryType) {
        return this.repositoryManager.getRepository(repositoryType);
    }

    @Override
    protected final void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        inject();
        doFilter(request, response, filterChain);
    }

    protected abstract void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;

    protected abstract void inject();
}
