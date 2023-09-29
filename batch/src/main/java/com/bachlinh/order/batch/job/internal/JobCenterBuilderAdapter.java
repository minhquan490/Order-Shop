package com.bachlinh.order.batch.job.internal;

import org.springframework.util.StringUtils;
import com.bachlinh.order.batch.boot.JobCenterBooster;
import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.exception.system.batch.JobCenterInitializeException;
import com.bachlinh.order.core.container.DependenciesResolver;

class JobCenterBuilderAdapter implements JobCenter.Builder {
    private final JobCenter.Builder delegate;
    private String profile;
    private DependenciesResolver dependenciesResolver;

    private JobCenterBuilderAdapter(JobCenter.Builder delegate) {
        this.delegate = delegate;
    }

    @Override
    public JobCenter.Builder dependenciesResolver(DependenciesResolver dependenciesResolver) {
        this.dependenciesResolver = dependenciesResolver;
        return this;
    }

    public JobCenter.Builder profile(String profile) {
        this.profile = profile;
        return this;
    }

    @Override
    public JobCenter build() {
        if (delegate instanceof JobCenterBooster booster) {
            this.profile = booster.getProfile();
            this.dependenciesResolver = booster.getDependenciesResolver();
        }
        if (!StringUtils.hasText(profile)) {
            throw new JobCenterInitializeException("Profile must be specify");
        }
        if (dependenciesResolver == null) {
            throw new JobCenterInitializeException("DependenciesResolver must be specific");
        }
        JobCenterDecorator decorator = new JobCenterDecorator(delegate.build(), profile, dependenciesResolver);
        return decorator.decorate(new ApplicationScanner());
    }

    static JobCenterBuilderAdapter wrap(JobCenter.Builder delegate) {
        return new JobCenterBuilderAdapter(delegate);
    }
}
