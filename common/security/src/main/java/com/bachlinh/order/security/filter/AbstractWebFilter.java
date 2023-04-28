package com.bachlinh.order.security.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Base filter for all filters used in this project. If you want to filter
 * follow flow backend to frontend, please confirm to tech lead first.
 *
 * @author Hoang Minh Quan
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractWebFilter extends OncePerRequestFilter {
    private final ApplicationContext applicationContext;

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
}
