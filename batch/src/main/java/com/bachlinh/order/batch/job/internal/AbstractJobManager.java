package com.bachlinh.order.batch.job.internal;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.scheduling.Trigger;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.boot.JobCenterBooster;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.container.DependenciesResolver;

public abstract class AbstractJobManager implements JobManager {

    private final JobCenter jobCenter;
    private final ThreadPoolManager threadPoolManager;
    protected final Collection<Report> reports = new ConcurrentLinkedDeque<>();

    protected AbstractJobManager(JobCenter.Builder jobCenterBuilder, String profile, DependenciesResolver dependenciesResolver) {
        if (jobCenterBuilder instanceof JobCenterBooster) {
            this.jobCenter = JobCenterBuilderAdapter.wrap(jobCenterBuilder).build();
        } else {
            this.jobCenter = jobCenterBuilder.profile(profile).dependenciesResolver(dependenciesResolver).build();
        }
        this.threadPoolManager = dependenciesResolver.resolveDependencies(ThreadPoolManager.class);
    }

    @Override
    public final Job getJob(String name) {
        return jobCenter.getJob(name);
    }

    @Override
    public final JobCenter getJobCenter() {
        return jobCenter;
    }

    @Override
    public final Collection<Report> getAllReport() {
        try {
            return reports;
        } finally {
            reports.clear();
        }
    }

    @Override
    public final void executeJob(String jobName) {
        jobCenter.executeJob(jobName);
        Report report = jobCenter.exportReport(jobName);
        reports.add(report);
    }

    @Override
    public final Report exportReport(String jobName) {
        return jobCenter.exportReport(jobName);
    }

    @Override
    public final void startJob() {
        Collection<Job> executionOnceJob = jobCenter.getJobExecuteOnce();
        for (Job job : executionOnceJob) {
            job.execute();
            Report report = job.getJobReport();
            reports.add(report);
        }
        threadPoolManager.schedule(() -> {
            Collection<Job> deadlineJobs = jobCenter.getDeadlineJobs();
            for (Job job : deadlineJobs) {
                executeJob(job.getName());
            }
            saveReport();
        }, getTrigger());
        saveReport();
    }

    protected abstract void saveReport();

    protected abstract Trigger getTrigger();
}
