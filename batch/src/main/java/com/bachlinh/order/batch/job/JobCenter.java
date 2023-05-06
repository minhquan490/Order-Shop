package com.bachlinh.order.batch.job;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Collection;

public sealed interface JobCenter extends JobRegistry, JobOperation, JobHolder permits AbstractJobCenter {

    Collection<Job> getDailyJob();

    Collection<Job> getMonthlyJob();

    Collection<Job> getYearlyJob();

    Collection<Job> getJobExecuteOnce();

    Collection<Report> getAllReport();

    interface Builder {
        Builder dependenciesResolver(DependenciesResolver dependenciesResolver);

        Builder profile(String profile);

        JobCenter build();
    }
}
