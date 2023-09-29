package com.bachlinh.order.handler.service;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.environment.Environment;

public abstract non-sealed class AbstractService implements ServiceBase {

    private final DependenciesResolver resolver;
    private final Environment environment;
    private final RepositoryManager repositoryManager;

    protected AbstractService(DependenciesResolver resolver, Environment environment) {
        this.resolver = resolver;
        this.environment = environment;
        this.repositoryManager = resolver.resolveDependencies(RepositoryManager.class);
    }

    @Override
    public DependenciesResolver getResolver() {
        return this.resolver;
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    protected RepositoryManager getRepositoryManager() {
        return this.repositoryManager;
    }

    protected <T> T resolveRepository(Class<T> repositoryType) {
        return getRepositoryManager().getRepository(repositoryType);
    }
}
