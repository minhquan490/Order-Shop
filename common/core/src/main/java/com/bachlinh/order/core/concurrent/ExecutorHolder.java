package com.bachlinh.order.core.concurrent;

import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.Executor;

public interface ExecutorHolder {
    Executor getEntityIndexExecutor();

    Executor getHttpExecutor();

    Executor getServiceExecutor();

    Executor getAsyncTaskExecutor();

    TaskScheduler getTaskScheduler();
}
