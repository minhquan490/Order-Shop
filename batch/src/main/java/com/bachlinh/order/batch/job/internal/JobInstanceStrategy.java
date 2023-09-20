package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.exception.system.batch.JobBuilderException;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.UnsafeUtils;

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
        AbstractJob abstractJob;
        try {
            abstractJob = (AbstractJob) UnsafeUtils.allocateInstance(jobType);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        return abstractJob.newInstance(batchJob.name(), profile, dependenciesResolver);
    }
}
