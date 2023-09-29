package com.bachlinh.order.security.filter.grpc;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.exception.http.AccessDeniedException;
import com.bachlinh.order.security.filter.GrpcWebFilter;
import com.bachlinh.order.security.handler.AccessDeniedHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.Collection;

public class PermissionFilter extends GrpcWebFilter {
    private PathMatcher pathMatcher;
    private Collection<String> excludePaths;
    private final String adminPath;
    private AccessDeniedHandler accessDeniedHandler;

    public PermissionFilter(DependenciesResolver dependenciesResolver, String profile) {
        super(dependenciesResolver, profile);
        this.adminPath = getEnvironment().getProperty("shop.url.admin");
    }

    @Override
    public PermissionFilter setExcludePaths(Collection<String> excludePaths) {
        this.excludePaths = excludePaths;
        return this;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void intercept(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!customer.getRole().equalsIgnoreCase(Role.ADMIN.name()) && pathMatcher.match(adminPath, request.getRequestURI())) {
            accessDeniedHandler.handle(response, new AccessDeniedException("Contact to admin for access to this url"));
        }
    }

    @Override
    protected void inject() {
        if (accessDeniedHandler == null) {
            accessDeniedHandler = resolveDependencies(AccessDeniedHandler.class);
        }
        if (pathMatcher == null) {
            pathMatcher = resolveDependencies(PathMatcher.class);
        }
    }
}
