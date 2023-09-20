package com.bachlinh.order.core.concurrent.support;

import com.bachlinh.order.core.concurrent.ExecutorHolder;
import com.bachlinh.order.core.concurrent.ThreadPoolOption;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

public class ExecutorContainer implements ExecutorHolder {

    private final ThreadPoolOption option;

    private ThreadPoolTaskExecutor entityIndexExecutor;
    private ThreadPoolTaskExecutor httpExecutor;
    private ThreadPoolTaskExecutor serviceExecutor;
    private ThreadPoolTaskExecutor asyncExecutor;
    private ThreadPoolTaskScheduler taskScheduler;

    public ExecutorContainer(ThreadPoolOption option) {
        this.option = option;
    }

    @Override
    public Executor getEntityIndexExecutor() {
        if (entityIndexExecutor == null) {
            this.entityIndexExecutor = createThreadPoolExecutor(this.option.getIndexExecutorCorePoolSize());
        }
        return this.entityIndexExecutor;
    }

    @Override
    public Executor getHttpExecutor() {
        if (httpExecutor == null) {
            this.httpExecutor = createThreadPoolExecutor(this.option.getHttpExecutorCorePoolSize());
        }
        return this.httpExecutor;
    }

    @Override
    public Executor getServiceExecutor() {
        if (serviceExecutor == null) {
            this.serviceExecutor = createThreadPoolExecutor(this.option.getServiceExecutorCorePoolSize());
        }
        return this.serviceExecutor;
    }

    @Override
    public Executor getAsyncTaskExecutor() {
        if (asyncExecutor == null) {
            this.asyncExecutor = createThreadPoolExecutor(this.option.getAsyncExecutorCorePoolSize());
        }
        return this.asyncExecutor;
    }

    @Override
    public TaskScheduler getTaskScheduler() {
        if (taskScheduler == null) {
            this.taskScheduler = createTaskScheduler(this.option.getSchedulerPoolSize());
        }
        return this.taskScheduler;
    }

    private ThreadPoolTaskExecutor createThreadPoolExecutor(int coreSize) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.initialize();
        executor.setThreadFactory(this.option.getVirtualThreadFactory());
        return executor;
    }

    private ThreadPoolTaskScheduler createTaskScheduler(int coreSize) {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(coreSize);
        scheduler.initialize();
        scheduler.setThreadFactory(this.option.getVirtualThreadFactory());
        return scheduler;
    }

    public ThreadPoolOption getOption() {
        return this.option;
    }
}
