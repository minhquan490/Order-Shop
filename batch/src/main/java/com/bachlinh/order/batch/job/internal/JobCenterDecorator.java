package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJobCenter;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.service.container.DependenciesResolver;

final class JobCenterDecorator {
    private final JobCenter jobCenter;
    private final String profile;
    private final DependenciesResolver dependenciesResolver;

    JobCenterDecorator(JobCenter jobCenter, String profile, DependenciesResolver dependenciesResolver) {
        this.jobCenter = jobCenter;
        this.profile = profile;
        this.dependenciesResolver = dependenciesResolver;
    }

    @SuppressWarnings("unchecked")
    AbstractJobCenter decorate(ApplicationScanner scanner) {
        AbstractJobCenter abstractJobCenter = (AbstractJobCenter) jobCenter;
        scanner.findComponents()
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(BatchJob.class))
                .filter(Job.class::isAssignableFrom)
                .toList()
                .forEach(clazz -> {
                    Class<? extends Job> jobClass = (Class<? extends Job>) clazz;
                    Job job = new JobInstanceStrategy(jobClass, profile, dependenciesResolver).newInstance();
                    abstractJobCenter.registerJob(job);
                });
        return abstractJobCenter;
    }
}
