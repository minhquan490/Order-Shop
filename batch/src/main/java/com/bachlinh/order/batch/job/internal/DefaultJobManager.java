package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.time.LocalDateTime;
import java.util.Collection;

class DefaultJobManager implements JobManager {
    private final JobCenter jobCenter;

    DefaultJobManager(JobCenter.Builder jobCenterBuilder, String profile, DependenciesResolver dependenciesResolver) {
        jobCenter = JobCenterBuilderAdapter.wrap(jobCenterBuilder)
                .profile(profile)
                .dependenciesResolver(dependenciesResolver)
                .build();
    }

    @Override
    public Job getJob(String name) {
        return jobCenter.getJob(name);
    }

    @Override
    public JobCenter getJobCenter() {
        return jobCenter;
    }

    @Override
    public Collection<Job> getDailyDeadlineJob() {
        LocalDateTime now = LocalDateTime.now();
        return jobCenter.getDailyJob()
                .stream()
                .filter(job -> now.isBefore(job.timeExecute()) || now.isEqual(job.timeExecute()))
                .toList();
    }

    @Override
    public Collection<Job> getMonthlyDeadlineJob() {
        LocalDateTime now = LocalDateTime.now();
        return jobCenter.getMonthlyJob()
                .stream()
                .filter(job -> now.isBefore(job.timeExecute()) || now.isEqual(job.timeExecute()))
                .toList();
    }

    @Override
    public Collection<Job> getYearlyDeadlineJob() {
        LocalDateTime now = LocalDateTime.now();
        return jobCenter.getYearlyJob()
                .stream()
                .filter(job -> now.isBefore(job.timeExecute()) || now.isEqual(job.timeExecute()))
                .toList();
    }

    @Override
    public void executeJob(String jobName) {
        jobCenter.executeJob(jobName);
    }

    @Override
    public Report exportReport(String jobName) {
        return jobCenter.exportReport(jobName);
    }
}