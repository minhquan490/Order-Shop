package com.bachlinh.order.security.filter;

import com.bachlinh.order.core.container.DependenciesResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

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
