package com.bachlinh.order.core.concurrent.support;

import com.bachlinh.order.core.concurrent.ExecutorHolder;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public class DefaultThreadPoolManager implements ThreadPoolManager {
    private final ExecutorHolder executorHolder;

    public DefaultThreadPoolManager(ThreadPoolOption option) {
        this.executorHolder = new ExecutorContainer(option);
    }

    @Override
    public void execute(Runnable runnable) {
        RunnableType defaultType = RunnableType.ASYNC;
        Executor executor = getExecutor(defaultType);
        executor.execute(runnable);
    }

    @Override
    public void execute(Runnable runnable, RunnableType type) {
        Executor executor = getExecutor(type);
        executor.execute(runnable);
    }

    @Override
    public Future<?> submit(Runnable task) {
        RunnableType defaultType = RunnableType.ASYNC;
        AsyncTaskExecutor executorService = (AsyncTaskExecutor) getExecutor(defaultType);
        return executorService.submit(task);
    }

    @Override
    public Future<?> submit(Runnable task, RunnableType type) {
        AsyncTaskExecutor executorService = (AsyncTaskExecutor) getExecutor(type);
        return executorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        RunnableType defaultType = RunnableType.ASYNC;
        AsyncTaskExecutor executorService = (AsyncTaskExecutor) getExecutor(defaultType);
        return executorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task, RunnableType type) {
        AsyncTaskExecutor executorService = (AsyncTaskExecutor) getExecutor(type);
        return executorService.submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        TaskScheduler scheduler = this.executorHolder.getTaskScheduler();
        return scheduler.schedule(task, trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        TaskScheduler scheduler = this.executorHolder.getTaskScheduler();
        return scheduler.schedule(task, startTime);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        TaskScheduler scheduler = this.executorHolder.getTaskScheduler();
        return scheduler.scheduleAtFixedRate(task, startTime, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        TaskScheduler scheduler = this.executorHolder.getTaskScheduler();
        return scheduler.scheduleAtFixedRate(task, period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        TaskScheduler scheduler = this.executorHolder.getTaskScheduler();
        return scheduler.scheduleWithFixedDelay(task, startTime, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        TaskScheduler scheduler = this.executorHolder.getTaskScheduler();
        return scheduler.scheduleWithFixedDelay(task, delay);
    }

    private Executor getExecutor(RunnableType type) {
        return switch (type) {
            case HTTP -> this.executorHolder.getHttpExecutor();
            case ASYNC -> this.executorHolder.getAsyncTaskExecutor();
            case INDEX -> this.executorHolder.getEntityIndexExecutor();
            case SERVICE -> this.executorHolder.getServiceExecutor();
        };
    }
}
