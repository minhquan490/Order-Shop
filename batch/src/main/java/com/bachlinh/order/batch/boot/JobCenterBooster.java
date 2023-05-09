package com.bachlinh.order.batch.boot;

import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.batch.job.internal.DefaultJobCenter;
import com.bachlinh.order.service.container.DependenciesResolver;

public class JobCenterBooster implements JobCenter.Builder {
    private DependenciesResolver dependenciesResolver;
    private String profile;

    public JobCenterBooster(DependenciesResolver dependenciesResolver, String profile) {
        this.dependenciesResolver = dependenciesResolver;
        this.profile = profile;
    }

    @Override
    public JobCenter.Builder dependenciesResolver(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
        return this;
    }

    @Override
    public JobCenter.Builder profile(String profile) {
        this.profile = profile;
        return this;
    }

    @Override
    public JobCenter build() {
        return new DefaultJobCenter(dependenciesResolver, profile);
    }

    public DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    public String getProfile() {
        return profile;
    }
}
