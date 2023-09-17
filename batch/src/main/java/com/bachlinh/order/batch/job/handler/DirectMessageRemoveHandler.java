package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.repository.DirectMessageRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.time.LocalDateTime;
import java.util.Collection;

@BatchJob(name = "directMessageRemoveHandler")
@ActiveReflection
public class DirectMessageRemoveHandler extends AbstractJob {
    private static final int REMOVAL_POLICY = 3;
    private LocalDateTime previousTimeExecution;

    private DirectMessageRepository directMessageRepository;

    private DirectMessageRemoveHandler(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    public AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        return new DirectMessageRemoveHandler(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (directMessageRepository == null) {
            directMessageRepository = getDependenciesResolver().resolveDependencies(DirectMessageRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() throws Exception {
        Collection<DirectMessage> directMessages = directMessageRepository.getDirectForRemove();
        directMessageRepository.deleteMessage(directMessages);
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
