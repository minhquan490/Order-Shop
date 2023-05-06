package com.bachlinh.order.batch.job;

public interface JobRegistry {
    void registerJob(Job job);

    void unRegisterJob(Job job);
}
