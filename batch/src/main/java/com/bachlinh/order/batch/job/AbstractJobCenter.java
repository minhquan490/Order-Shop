package com.bachlinh.order.batch.job;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.batch.JobExistedException;
import com.bachlinh.order.core.exception.system.batch.JobNotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract non-sealed class AbstractJobCenter implements JobCenter {
    private final Map<String, Job> jobContext;
    private final DependenciesResolver dependenciesResolver;
    private final Environment environment;

    protected AbstractJobCenter(DependenciesResolver dependenciesResolver, String profile) {
        this.jobContext = new ConcurrentHashMap<>();
        this.dependenciesResolver = dependenciesResolver;
        this.environment = Environment.getInstance(profile);
    }

    @Override
    public final void unRegisterJob(Job job) {
        Job registedJob = jobContext.get(job.getName());
        if (registedJob == null) {
            String message = STR. "Job with name [\{ job.getName() }] not registered" ;
            throw new JobNotFoundException(message);
        }
        jobContext.remove(job.getName(), job);
    }

    @Override
    public final void registerJob(Job job) {
        if (jobContext.containsKey(job.getName())) {
            throw new JobExistedException("Job with name [" + job.getName() + "] had been existed");
        }
        jobContext.put(job.getName(), job);
    }

    @Override
    public Collection<Job> getDeadlineJobs() {
        Collection<Job> results = new LinkedList<>();

        Collection<Job> dailyJobs = getDailyJob();
        Collection<Job> monthlyJobs = getMonthlyJob();
        Collection<Job> yearlyJobs = getYearlyJob();

        LocalDateTime now = LocalDateTime.now();
        findDeadlineJobs(dailyJobs, results, now);
        findDeadlineJobs(monthlyJobs, results, now);
        findDeadlineJobs(yearlyJobs, results, now);

        return results;
    }

    private void findDeadlineJobs(Collection<Job> currentJobs, Collection<Job> holder, LocalDateTime now) {
        for (Job job : currentJobs) {
            LocalDateTime next = job.getNextExecutionTime();
            if (next.isBefore(now) || next.isEqual(now)) {
                holder.add(job);
            }
        }
    }

    protected Map<String, Job> getJobContext() {
        return jobContext;
    }

    protected DependenciesResolver getDependenciesResolver() {
        return dependenciesResolver;
    }

    protected Environment getEnvironment() {
        return environment;
    }
}
