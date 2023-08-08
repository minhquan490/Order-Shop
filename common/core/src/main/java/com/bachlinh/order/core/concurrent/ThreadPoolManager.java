package com.bachlinh.order.core.concurrent;

import org.springframework.scheduling.Trigger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

public interface ThreadPoolManager {
    void execute(Runnable runnable);

    void execute(Runnable runnable, RunnableType type);

    Future<?> submit(Runnable task);

    Future<?> submit(Runnable task, RunnableType type);

    <T> Future<T> submit(Callable<T> task);

    <T> Future<T> submit(Callable<T> task, RunnableType type);

    ScheduledFuture<?> schedule(Runnable task, Trigger trigger);

    ScheduledFuture<?> schedule(Runnable task, Instant startTime);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay);

}
