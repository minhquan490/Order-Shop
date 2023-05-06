package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.exception.system.batch.JobBuilderException;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.lang.reflect.Constructor;

final class JobInstanceStrategy {
    private final Class<? extends Job> jobType;
    private final String profile;
    private final DependenciesResolver dependenciesResolver;

    JobInstanceStrategy(Class<? extends Job> jobType, String profile, DependenciesResolver dependenciesResolver) {
        if (!jobType.isAnnotationPresent(BatchJob.class)) {
            throw new JobBuilderException("Job type must be annotated by @BatchJob");
        }
        this.jobType = jobType;
        this.profile = profile;
        this.dependenciesResolver = dependenciesResolver;
    }

    public Job newInstance() {
        BatchJob batchJob = jobType.getAnnotation(BatchJob.class);
        try {
            Constructor<? extends Job> constructor = jobType.getConstructor(String.class, String.class, DependenciesResolver.class);
            return constructor.newInstance(batchJob.name(), profile, dependenciesResolver);
        } catch (Exception e) {
            throw new JobBuilderException("Can not build Job", e);
        }
    }
}
