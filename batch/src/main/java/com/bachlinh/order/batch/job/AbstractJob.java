package com.bachlinh.order.batch.job;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.entity.repository.RepositoryManager;

import java.time.LocalDateTime;

public abstract non-sealed class AbstractJob implements Job {
    private final String name;
    private Report report;
    private final Environment environment;
    private final DependenciesResolver dependenciesResolver;
    private final RepositoryManager repositoryManager;
    private boolean executed = false;

    protected AbstractJob(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        this.name = name;
        this.report = new Report(name);
        this.environment = Environment.getInstance(activeProfile);
        this.dependenciesResolver = dependenciesResolver;
        this.repositoryManager = resolveDependencies(RepositoryManager.class);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        inject();
        try {
            doExecuteInternal();
        } catch (Exception e) {
            addException(e);
        } finally {
            this.executed = true;
        }
    }

    @Override
    public final Report getJobReport() {
        try {
            return report;
        } finally {
            report = new Report(name);
        }
    }

    @Override
    public final LocalDateTime getNextExecutionTime() {
        if (!executed) {
            return doGetNextExecutionTime();
        }
        return getPreviousExecutionTime();
    }

    @Override
    public final LocalDateTime getPreviousExecutionTime() {
        return doGetPreviousExecutionTime();
    }

    public abstract AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver);

    protected void addException(Exception exception) {
        this.report.addError(exception);
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected <T> T resolveDependencies(Class<T> dependenciesType) {
        return getDependenciesResolver().resolveDependencies(dependenciesType);
    }

    protected <T> T resolveRepository(Class<T> repositoryType) {
        return this.repositoryManager.getRepository(repositoryType);
    }

    protected abstract void inject();

    protected abstract void doExecuteInternal() throws Exception;

    protected abstract LocalDateTime doGetNextExecutionTime();

    protected abstract LocalDateTime doGetPreviousExecutionTime();
}
