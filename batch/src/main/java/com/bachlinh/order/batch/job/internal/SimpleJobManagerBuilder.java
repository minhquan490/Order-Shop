package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.service.container.DependenciesResolver;

public class SimpleJobManagerBuilder implements JobManager.Builder {
    private DependenciesResolver dependenciesResolver;
    private String profile;

    @Override
    public JobManager.Builder dependenciesResolver(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
        return this;
    }

    @Override
    public JobManager.Builder profile(String profile) {
        this.profile = profile;
        return this;
    }

    @Override
    public JobManager build(JobCenter.Builder jobCenterBuilder) {
        return new DefaultJobManager(jobCenterBuilder, profile, dependenciesResolver);
    }
}
