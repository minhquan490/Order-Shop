package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ActiveReflection
@BatchJob(name = "customerAccessHistoryCleaner")
public class CustomerAccessHistoryCleaner extends AbstractJob {
    private static final int REMOVAL_POLICY = 1;
    private LocalDateTime previousTimeExecution;
    private CustomerAccessHistoryRepository repository;

    @ActiveReflection
    public CustomerAccessHistoryCleaner(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (repository == null) {
            repository = getDependenciesResolver().resolveDependencies(CustomerAccessHistoryRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() throws Exception {
        var histories = repository.getHistoriesExpireNow(Date.valueOf(LocalDate.now()));
        repository.deleteAll(histories);
        this.previousTimeExecution = LocalDateTime.now();
    }

    @Override
    protected LocalDateTime doGetNextExecutionTime() {
        return getPreviousExecutionTime().plusYears(REMOVAL_POLICY);
    }

    @Override
    protected LocalDateTime doGetPreviousExecutionTime() {
        if (previousTimeExecution == null) {
            previousTimeExecution = LocalDateTime.now();
        }
        return previousTimeExecution;
    }

    @Override
    public JobType getJobType() {
        return JobType.YEARLY;
    }
}
