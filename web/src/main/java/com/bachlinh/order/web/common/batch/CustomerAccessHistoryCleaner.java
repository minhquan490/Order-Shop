package com.bachlinh.order.web.common.batch;

import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.BatchJob;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ActiveReflection
@BatchJob(name = "customerAccessHistoryCleaner")
public class CustomerAccessHistoryCleaner extends AbstractJob {
    private static final int REMOVAL_POLICY = 1;
    private LocalDateTime previousTimeExecution;
    private CustomerAccessHistoryRepository repository;

    private CustomerAccessHistoryCleaner(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    public AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        return new CustomerAccessHistoryCleaner(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (repository == null) {
            repository = resolveRepository(CustomerAccessHistoryRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() {
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
