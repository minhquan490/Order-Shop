package com.bachlinh.order.batch.job.internal;

import lombok.extern.slf4j.Slf4j;
import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.boot.JobCenterBooster;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.batch.job.Worker;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Collection;
import java.util.LinkedList;

@Slf4j
class DefaultJobManager implements JobManager {

    private final JobCenter jobCenter;
    private final Collection<Worker> workers = new LinkedList<>();

    DefaultJobManager(JobCenter.Builder jobCenterBuilder, String profile, DependenciesResolver dependenciesResolver) {
        if (jobCenterBuilder instanceof JobCenterBooster) {
            this.jobCenter = JobCenterBuilderAdapter.wrap(jobCenterBuilder).build();
        } else {
            this.jobCenter = jobCenterBuilder.profile(profile).dependenciesResolver(dependenciesResolver).build();
        }
        workers.addAll(jobCenter.getDailyJob().stream().map(job -> {
            Worker worker = new DefaultJobWorker(job);
            writeLog(job);
            return worker;
        }).toList());
        workers.addAll(jobCenter.getMonthlyJob().stream().map(job -> {
            Worker worker = new DefaultJobWorker(job);
            writeLog(job);
            return worker;
        }).toList());
        workers.addAll(jobCenter.getYearlyJob().stream().map(job -> {
            Worker worker = new DefaultJobWorker(job);
            writeLog(job);
            return worker;
        }).toList());
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
    public Collection<Report> getAllReport() {
        Collection<Report> reports = new LinkedList<>();
        workers.forEach(worker -> reports.addAll(worker.getAllReport()));
        return reports;
    }

    @Override
    public void executeJob(String jobName) {
        jobCenter.executeJob(jobName);
    }

    @Override
    public Report exportReport(String jobName) {
        return jobCenter.exportReport(jobName);
    }

    private void writeLog(Job job) {
        if (log.isDebugEnabled()) {
            log.debug("Subscribe job [{}]", job.getName());
        }
    }
}