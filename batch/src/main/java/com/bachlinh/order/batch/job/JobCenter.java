package com.bachlinh.order.batch.job;

import com.bachlinh.order.core.container.DependenciesResolver;

import java.util.Collection;

public sealed interface JobCenter extends JobRegistry, JobOperation, JobHolder permits AbstractJobCenter {

    Collection<Job> getDeadlineJobs();

    Collection<Job> getDailyJob();

    Collection<Job> getMonthlyJob();

    Collection<Job> getYearlyJob();

    Collection<Job> getJobExecuteOnce();

    interface Builder {
        Builder dependenciesResolver(DependenciesResolver dependenciesResolver);

        Builder profile(String profile);

        JobCenter build();
    }
}
