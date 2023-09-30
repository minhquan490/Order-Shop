package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.repository.EmailRepository;
import com.bachlinh.order.repository.EmailTrashRepository;

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

    private EmailTrashClearing(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    public AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        return new EmailTrashClearing(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (emailRepository == null) {
            emailRepository = resolveRepository(EmailRepository.class);
        }
        if (emailTrashRepository == null) {
            emailTrashRepository = resolveRepository(EmailTrashRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() {
        var trashes = emailTrashRepository.getTrashNeedClearing();
        var emails = new ArrayList<Email>();
        for (var trash : trashes) {
            emails.addAll(trash.getEmails());
            trash.setEmails(HashSet.newHashSet(0));
        }
        emailTrashRepository.updateTrashes(trashes);
        emailRepository.deleteEmails(emails.stream().map(Email::getId).toList());
        previousTimeExecution = LocalDateTime.now();
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
