package com.bachlinh.order.batch.job;

import com.bachlinh.order.batch.Report;

public interface JobOperation {
    void executeJob(String jobName);

    Report exportReport(String jobName);
}
