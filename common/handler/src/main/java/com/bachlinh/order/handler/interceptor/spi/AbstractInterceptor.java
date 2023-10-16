package com.bachlinh.order.handler.interceptor.spi;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.core.environment.Environment;

public abstract non-sealed class AbstractInterceptor implements WebInterceptor {
    private DependenciesResolver resolver;
    private Environment environment;
    private RepositoryManager repositoryManager;

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

    @Override
    public boolean isEnable() {
        return true;
    }

    public abstract AbstractInterceptor getInstance();

    public abstract void init();

    protected DependenciesResolver getResolver() {
        return this.resolver;
    }

    protected Environment getEnvironment() {
        return this.environment;
    }

    protected <T> T resolveRepository(Class<T> repositoryType) {
        if (repositoryManager == null) {
            repositoryManager = getResolver().resolveDependencies(RepositoryManager.class);
        }
        return repositoryManager.getRepository(repositoryType);
    }

    public void setResolver(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
