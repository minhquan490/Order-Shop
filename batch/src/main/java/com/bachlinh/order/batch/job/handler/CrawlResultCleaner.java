package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.repository.CrawlResultRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.time.LocalDateTime;

@BatchJob(name = "crawlResultCleaner")
@ActiveReflection
public class CrawlResultCleaner extends AbstractJob {
    private CrawlResultRepository crawlResultRepository;
    private LocalDateTime previousExecution;

    @ActiveReflection
    public CrawlResultCleaner(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (crawlResultRepository == null) {
            crawlResultRepository = getDependenciesResolver().resolveDependencies(CrawlResultRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        crawlResultRepository.deleteCrawlResults(now);
        previousExecution = now;
    }

    @Override
    protected LocalDateTime doGetNextExecutionTime() {
        return previousExecution.plusMonths(1);
    }

    @Override
    protected LocalDateTime doGetPreviousExecutionTime() {
        if (previousExecution == null) {
            previousExecution = LocalDateTime.now();
        }
        return previousExecution;
    }

    @Override
    public JobType getJobType() {
        return JobType.MONTHLY;
    }
}
