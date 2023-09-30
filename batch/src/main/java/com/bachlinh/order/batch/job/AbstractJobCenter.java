package com.bachlinh.order.batch.job;

import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.batch.JobExistedException;
import com.bachlinh.order.core.exception.system.batch.JobNotFoundException;
import com.bachlinh.order.core.container.DependenciesResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract non-sealed class AbstractJobCenter implements JobCenter {
    private final Map<String, Job> jobContext;
    private final DependenciesResolver dependenciesResolver;
    private final Environment environment;

    protected AbstractJobCenter(DependenciesResolver dependenciesResolver, String profile) {
        this.jobContext = new ConcurrentHashMap<>();
        this.dependenciesResolver = dependenciesResolver;
        this.environment = Environment.getInstance(profile);
    }

    @Override
    public final void unRegisterJob(Job job) {
        Job registedJob = jobContext.get(job.getName());
        if (registedJob == null) {
            throw new JobNotFoundException("Job with name [" + job.getName() + "] not registered");
        }
        jobContext.remove(job.getName(), job);
    }

    @Override
    public final void registerJob(Job job) {
        if (jobContext.containsKey(job.getName())) {
            throw new JobExistedException("Job with name [" + job.getName() + "] had been existed");
        }
        jobContext.put(job.getName(), job);
    }

    protected Map<String, Job> getJobContext() {
        return jobContext;
    }

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected Environment getEnvironment() {
        return environment;
    }
}
