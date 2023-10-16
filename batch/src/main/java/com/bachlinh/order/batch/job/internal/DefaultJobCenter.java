package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.job.AbstractJobCenter;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.exception.system.batch.JobNotFoundException;

import java.util.Collection;
import java.util.Comparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class DefaultJobCenter extends AbstractJobCenter {

    private final Comparator<Job> jobOrderComparator = Comparator.comparingInt(value -> extractOrder(value.getClass()));

    public DefaultJobCenter(DependenciesResolver dependenciesResolver, String profile) {
        super(dependenciesResolver, profile);
    }

    @Override
    public Collection<Job> getDailyJob() {
        return filterJob(JobType.DAILY);
    }

    @Override
    public Collection<Job> getMonthlyJob() {
        return filterJob(JobType.MONTHLY);
    }

    @Override
    public Collection<Job> getYearlyJob() {
        return filterJob(JobType.YEARLY);
    }

    @Override
    public Collection<Job> getJobExecuteOnce() {
        Collection<Job> jobs = filterJob(JobType.ONCE);
        jobs.forEach(job -> getJobContext().remove(job.getName(), job));
        return jobs;
    }

    @Override
    public Job getJob(String name) {
        Job job = getJobContext().get(name);
        if (job == null) {
            String message = STR. "Job with name [\{ name }] not registered" ;
            throw new JobNotFoundException(message);
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

    private int extractOrder(Class<?> jobType) {
        Order order = jobType.getAnnotation(Order.class);
        return order == null ? Ordered.LOWEST_PRECEDENCE : order.value();
    }

    private Collection<Job> filterJob(JobType type) {
        return getJobContext().values()
                .stream()
                .filter(job -> job.getJobType().equals(type))
                .sorted(jobOrderComparator)
                .toList();
    }
}
