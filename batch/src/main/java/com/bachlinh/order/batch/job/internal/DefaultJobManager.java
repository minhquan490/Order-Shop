package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.boot.JobCenterBooster;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.BatchReport;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.repository.BatchReportRepository;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.scheduling.support.CronTrigger;

class DefaultJobManager implements JobManager {

    private final JobCenter jobCenter;
    private final ThreadPoolManager threadPoolManager;
    private final Collection<Report> reports = new ConcurrentLinkedDeque<>();
    private final RepositoryManager repositoryManager;
    private final EntityFactory entityFactory;

    DefaultJobManager(JobCenter.Builder jobCenterBuilder, String profile, DependenciesResolver dependenciesResolver) {
        if (jobCenterBuilder instanceof JobCenterBooster) {
            this.jobCenter = JobCenterBuilderAdapter.wrap(jobCenterBuilder).build();
        } else {
            this.jobCenter = jobCenterBuilder.profile(profile).dependenciesResolver(dependenciesResolver).build();
        }
        this.threadPoolManager = dependenciesResolver.resolveDependencies(ThreadPoolManager.class);
        this.repositoryManager = dependenciesResolver.resolveDependencies(RepositoryManager.class);
        this.entityFactory = dependenciesResolver.resolveDependencies(EntityFactory.class);
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
        try {
            return reports;
        } finally {
            reports.clear();
        }
    }

    @Override
    public void startJob() {
        Collection<Job> executionOnceJob = jobCenter.getJobExecuteOnce();
        for (Job job : executionOnceJob) {
            job.execute();
            Report report = job.getJobReport();
            reports.add(report);
        }
        CronTrigger cronTrigger = new CronTrigger("0 0 */3 ? * *");
        threadPoolManager.schedule(() -> {
            Collection<Job> deadlineJobs = jobCenter.getDeadlineJobs();
            for (Job job : deadlineJobs) {
                executeJob(job.getName());
            }
            saveReport();
        }, cronTrigger);
        saveReport();
    }

    @Override
    public void executeJob(String jobName) {
        jobCenter.executeJob(jobName);
        Report report = jobCenter.exportReport(jobName);
        reports.add(report);
    }

    @Override
    public Report exportReport(String jobName) {
        return jobCenter.exportReport(jobName);
    }

    private void saveReport() {
        BatchReportRepository batchReportRepository = repositoryManager.getRepository(BatchReportRepository.class);
        Collection<BatchReport> batchReports = new LinkedList<>();
        for (Report report : reports) {
            BatchReport batchReport = entityFactory.getEntity(BatchReport.class);
            batchReport.setHasError(report.isHasError());
            batchReport.setErrorDetail(report.getError() == null ? "" : stackTraceToString(report.getError()));
            batchReport.setBatchName(report.getJobName());
            batchReport.setTimeReport(Timestamp.from(Instant.now()));
            batchReports.add(batchReport);
        }
        batchReportRepository.saveAllReport(batchReports);
        reports.clear();
    }

    private String stackTraceToString(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}