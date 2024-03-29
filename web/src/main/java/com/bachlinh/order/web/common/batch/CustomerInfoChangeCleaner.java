package com.bachlinh.order.web.common.batch;

import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.BatchJob;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.web.repository.spi.CustomerInfoChangeHistoryRepository;

import java.time.LocalDateTime;

@ActiveReflection
@BatchJob(name = "customerInfoChangeCleaner")
public class CustomerInfoChangeCleaner extends AbstractJob {
    private static final int REMOVAL_POLICY = 1;
    private LocalDateTime previousTimeExecution;
    private CustomerInfoChangeHistoryRepository repository;

    private CustomerInfoChangeCleaner(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    public AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        return new CustomerInfoChangeCleaner(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (repository == null) {
            repository = resolveRepository(CustomerInfoChangeHistoryRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() {
        var histories = repository.getHistoriesInYear();
        repository.deleteHistories(histories);
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
