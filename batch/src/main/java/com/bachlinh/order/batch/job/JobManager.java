package com.bachlinh.order.batch.job;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Collection;

public interface JobManager extends JobOperation, JobHolder {
    JobCenter getJobCenter();

    Collection<Report> getAllReport();

    interface Builder {
        Builder dependenciesResolver(DependenciesResolver dependenciesResolver);

        Builder profile(String profile);

        JobManager build(JobCenter.Builder jobCenterBuilder);
    }
}
