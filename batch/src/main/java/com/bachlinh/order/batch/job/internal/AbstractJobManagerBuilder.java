package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.exception.system.common.CriticalException;

public abstract class AbstractJobManagerBuilder implements JobManager.Builder {

    private DependenciesResolver dependenciesResolver;
    private String profile;

    @Override
    public final JobManager.Builder dependenciesResolver(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
        return this;
    }

    @Override
    public final JobManager.Builder profile(String profile) {
        this.profile = profile;
        return this;
    }

    @Override
    public final JobManager build(JobCenter.Builder jobCenterBuilder) {
        if (dependenciesResolver == null) {
            throw new CriticalException("Require dependencies resolver for build JobManager");
        }
        if (profile == null) {
            throw new CriticalException("Require profile activated for build JobManager");
        }
        if (jobCenterBuilder == null) {
            throw new CriticalException("Require job center builder for build JobManager");
        }
        return doBuild(dependenciesResolver, profile, jobCenterBuilder);
    }

    protected abstract JobManager doBuild(DependenciesResolver resolver, String profile, JobCenter.Builder builder);
}
