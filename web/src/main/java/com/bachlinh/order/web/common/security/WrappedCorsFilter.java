package com.bachlinh.order.web.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.security.filter.AbstractWebFilter;

import java.io.IOException;

import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class WrappedCorsFilter extends AbstractWebFilter {

    private final CorsFilter internalFilter;

    public WrappedCorsFilter(DependenciesResolver dependenciesResolver, CorsConfigurationSource corsConfigurationSource) {
        super(dependenciesResolver);
        this.internalFilter = new CorsFilter(corsConfigurationSource);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        this.internalFilter.doFilter(request, response, filterChain);
    }

    @Override
    protected void inject() {
        // Do nothing
    }
}
