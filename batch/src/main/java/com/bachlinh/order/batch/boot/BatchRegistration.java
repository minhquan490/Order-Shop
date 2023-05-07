package com.bachlinh.order.batch.boot;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.core.enums.ExecuteEvent;
import com.bachlinh.order.core.excecute.AbstractExecutor;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.BatchReport;
import com.bachlinh.order.repository.BatchReportRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.utils.JacksonUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

@ActiveReflection
public class BatchRegistration extends AbstractExecutor<JobManager> {

    private ThreadPoolTaskScheduler scheduler;
    private EntityFactory entityFactory;
    private BatchReportRepository batchReportRepository;

    @ActiveReflection
    public BatchRegistration(DependenciesContainerResolver containerResolver, String profile) {
        super(containerResolver, profile);
    }

    @Override
    protected void inject() {
        if (scheduler == null) {
            scheduler = getResolver().resolveDependencies(ThreadPoolTaskScheduler.class);
        }
        if (entityFactory == null) {
            entityFactory = getResolver().resolveDependencies(EntityFactory.class);
        }
        if (batchReportRepository == null) {
            batchReportRepository = getResolver().resolveDependencies(BatchReportRepository.class);
        }
    }

    @Override
    protected void doExecute(JobManager bootObject) {
        Trigger trigger = new CronTrigger("0 0 0 * * *");
        scheduler.schedule(getTask(bootObject), trigger);

        Collection<Job> jobs = bootObject.getJobCenter().getJobExecuteOnce();
        jobs.forEach(job -> scheduler.schedule(getOnceTask(job), job.timeExecute().toInstant(ZoneOffset.UTC)));
    }

    @Override
    public ExecuteEvent runOn() {
        return ExecuteEvent.ON_READY;
    }

    @Override
    public Class<JobManager> getBootObjectType() {
        return JobManager.class;
    }

    private Runnable getTask(JobManager jobManager) {
        return () -> {
            Collection<Job> dailyJob = jobManager.getDailyDeadlineJob();
            dailyJob.forEach(job -> jobManager.executeJob(job.getName()));

            Collection<Job> monthlyJob = jobManager.getMonthlyDeadlineJob();
            monthlyJob.forEach(job -> jobManager.executeJob(job.getName()));

            Collection<Job> yearlyJob = jobManager.getYearlyDeadlineJob();
            yearlyJob.forEach(job -> jobManager.executeJob(job.getName()));

            List<BatchReport> batchReports = jobManager.getAllReport()
                    .stream()
                    .map(this::processReport).toList();
            batchReportRepository.saveAllReport(batchReports);
        };
    }

    private Runnable getOnceTask(Job job) {
        return () -> {
            job.execute();
            Report report = job.getJobReport();
            BatchReport batchReport = processReport(report);
            batchReportRepository.saveAllReport(List.of(batchReport));
        };
    }

    private BatchReport processReport(Report report) {
        BatchReport batchReport = entityFactory.getEntity(BatchReport.class);
        batchReport.setBatchName(report.getJobName());
        batchReport.setHasError(report.isHasError());
        if (report.isHasError()) {
            batchReport.setErrorDetail(JacksonUtils.writeObjectAsString(report.getError()));
        }
        batchReport.setTimeReport(Timestamp.from(Instant.now()));
        return batchReport;
    }
}
