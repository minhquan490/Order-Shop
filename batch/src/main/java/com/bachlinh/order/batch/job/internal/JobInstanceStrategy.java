package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.core.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.exception.system.batch.JobBuilderException;

final class JobInstanceStrategy {
    private final Class<? extends Job> jobType;
    private final String profile;
    private final DependenciesResolver dependenciesResolver;
    private final Initializer<AbstractJob> initializer = new BatchJobInitializer();

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

        return initializer.getObject(jobType, batchJob.name(), profile, dependenciesResolver);
    }
}
