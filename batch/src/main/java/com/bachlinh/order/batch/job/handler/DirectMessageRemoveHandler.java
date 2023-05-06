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
    private static final int REMOVAL_POLICY = -3;

    private DirectMessageRepository directMessageRepository;

    @ActiveReflection
    public DirectMessageRemoveHandler(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
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
    }

    @Override
    public LocalDateTime timeExecute() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.plusYears(REMOVAL_POLICY);
    }

    @Override
    public JobType getJobType() {
        return JobType.YEARLY;
    }
}
