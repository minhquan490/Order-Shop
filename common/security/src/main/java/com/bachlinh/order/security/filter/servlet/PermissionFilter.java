package com.bachlinh.order.security.filter.servlet;

import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.http.AccessDeniedException;
import com.bachlinh.order.security.filter.AbstractWebFilter;
import com.bachlinh.order.security.handler.AccessDeniedHandler;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class PermissionFilter extends AbstractWebFilter {
    private static final Collection<Role> ROLES_ALLOW_ACCESS_ADMIN = getRolesAllowAccessAdmin();
    private final PathMatcher pathMatcher;
    private final Collection<String> excludePaths;
    private final String adminPath;
    private AccessDeniedHandler accessDeniedHandler;

    public PermissionFilter(DependenciesContainerResolver containerResolver, String profile, Collection<String> excludePaths, PathMatcher pathMatcher) {
        super(containerResolver.getDependenciesResolver());
        Environment environment = Environment.getInstance(profile);
        this.excludePaths = excludePaths;
        this.pathMatcher = pathMatcher;
        this.adminPath = environment.getProperty("shop.url.admin");
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (pathMatcher.match(adminPath, request.getRequestURI())) {
            boolean canAccessAdmin = ROLES_ALLOW_ACCESS_ADMIN.contains(Role.of(customer.getRole()));
            if (canAccessAdmin) {
                filterChain.doFilter(request, response);
            } else {
                accessDeniedHandler.handle(response, new AccessDeniedException("Contact to admin for access to this url"));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected void inject() {
        if (accessDeniedHandler == null) {
            accessDeniedHandler = getDependenciesResolver().resolveDependencies(AccessDeniedHandler.class);
        }
    }

    private static Collection<Role> getRolesAllowAccessAdmin() {
        return List.of(Role.ADMIN, Role.SEO, Role.MARKETING);
    }
}
