package com.bachlinh.order.handler.interceptor.spi;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;

public abstract non-sealed class AbstractInterceptor implements WebInterceptor {
    private DependenciesResolver resolver;
    private Environment environment;

    protected AbstractInterceptor() {
    }

    @Override
    public boolean preHandle(NativeRequest<?> request, NativeResponse<?> response) {
        return true;
    }

    @Override
    public void postHandle(NativeRequest<?> request, NativeResponse<?> response) {
        // Do nothing in abstract
    }

    @Override
    public void onComplete(NativeRequest<?> request, NativeResponse<?> response) {
        // Do nothing in abstract
    }

    public abstract AbstractInterceptor getInstance();

    public abstract void init();

    protected DependenciesResolver getResolver() {
        return this.resolver;
    }

    protected Environment getEnvironment() {
        return this.environment;
    }

    public void setResolver(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
