package com.bachlinh.order.batch.job;

import com.bachlinh.order.batch.Report;

import java.time.LocalDateTime;

public sealed interface Job permits AbstractJob {
    String getName();

    LocalDateTime timeExecute();

    void execute();

    Report getJobReport();

    JobType getJobType();
}
