package com.bachlinh.order.dto.strategy;

import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;

public abstract non-sealed class AbstractDtoStrategy<T, U> implements DtoStrategy<T, U> {
    private final DependenciesResolver dependenciesResolver;
    private final Environment environment;

    protected AbstractDtoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        this.dependenciesResolver = dependenciesResolver;
        this.environment = environment;
    }

    @Override
    public final T convert(U source, Class<T> type) {
        inject();
        beforeConvert(source, type);
        var result = doConvert(source, type);
        afterConvert(source, type);
        return result;
    }

    public final DtoStrategy<T, U> getInstance(DependenciesResolver dependenciesResolver, Environment environment) {
        return createNew(dependenciesResolver, environment);
    }

    protected void inject() {
    }

    protected abstract void beforeConvert(U source, Class<T> type);

    protected abstract T doConvert(U source, Class<T> type);

    protected abstract void afterConvert(U source, Class<T> type);

    protected abstract DtoStrategy<T, U> createNew(DependenciesResolver dependenciesResolver, Environment environment);

    public DependenciesResolver getDependenciesResolver() {
        return this.dependenciesResolver;
    }

    public Environment getEnvironment() {
        return this.environment;
    }
}
