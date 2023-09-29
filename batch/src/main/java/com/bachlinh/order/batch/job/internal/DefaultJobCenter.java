package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.job.AbstractJobCenter;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.exception.system.batch.JobNotFoundException;
import com.bachlinh.order.core.container.DependenciesResolver;

import java.util.Collection;

public class DefaultJobCenter extends AbstractJobCenter {

    public DefaultJobCenter(DependenciesResolver dependenciesResolver, String profile) {
        super(dependenciesResolver, profile);
    }

    @Override
    public Collection<Job> getDailyJob() {
        return getJobContext().values()
                .stream()
                .filter(job -> job.getJobType().equals(JobType.DAILY))
                .toList();
    }

    @Override
    public Collection<Job> getMonthlyJob() {
        return getJobContext().values()
                .stream()
                .filter(job -> job.getJobType().equals(JobType.MONTHLY))
                .toList();
    }

    @Override
    public Collection<Job> getYearlyJob() {
        return getJobContext().values()
                .stream()
                .filter(job -> job.getJobType().equals(JobType.YEARLY))
                .toList();
    }

    @Override
    public Collection<Job> getJobExecuteOnce() {
        Collection<Job> jobs = getJobContext().values()
                .stream()
                .filter(job -> job.getJobType().equals(JobType.ONCE))
                .toList();
        jobs.forEach(job -> getJobContext().remove(job.getName(), job));
        return jobs;
    }

    @Override
    public Job getJob(String name) {
        Job job = getJobContext().get(name);
        if (job == null) {
            throw new JobNotFoundException("Job with name [" + name + "] not registered");
        }
        return job;
    }

    @Override
    public void executeJob(String jobName) {
        getJob(jobName).execute();
    }

    @Override
    public Report exportReport(String jobName) {
        return getJob(jobName).getJobReport();
    }
}
