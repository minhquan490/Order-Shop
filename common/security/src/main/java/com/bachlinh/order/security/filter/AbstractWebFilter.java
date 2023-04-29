package com.bachlinh.order.security.filter;

import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Base filter for all filters used in this project. If you want to filter
 * follow flow backend to frontend, please confirm to tech lead first.
 *
 * @author Hoang Minh Quan
 */
@Getter
public abstract class AbstractWebFilter extends OncePerRequestFilter {
    private final ApplicationContext applicationContext;

    protected AbstractWebFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
