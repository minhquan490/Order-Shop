package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.repository.EmailRepository;
import com.bachlinh.order.repository.EmailTrashRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@ActiveReflection
@BatchJob(name = "emailTrashClearing")
public class EmailTrashClearing extends AbstractJob {
    private static final int REMOVAL_POLICY = 1;
    private LocalDateTime previousTimeExecution;
    private EmailRepository emailRepository;
    private EmailTrashRepository emailTrashRepository;

    @ActiveReflection
    public EmailTrashClearing(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (emailRepository == null) {
            emailRepository = getDependenciesResolver().resolveDependencies(EmailRepository.class);
        }
        if (emailTrashRepository == null) {
            emailTrashRepository = getDependenciesResolver().resolveDependencies(EmailTrashRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() throws Exception {
        var trashes = emailTrashRepository.getTrashNeedClearing();
        var emails = new ArrayList<Email>();
        for (var trash : trashes) {
            emails.addAll(trash.getEmails());
            trash.setEmails(new HashSet<>(0));
        }
        emailTrashRepository.updateTrashes(trashes);
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
        return JobType.MONTHLY;
    }
}
