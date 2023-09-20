package com.bachlinh.order.core.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

public final class ThreadPoolOption {
    private int schedulerPoolSize = 1;
    private int asyncExecutorCorePoolSize = 1;
    private int httpExecutorCorePoolSize = 1;
    private int serviceExecutorCorePoolSize = 1;
    private int indexExecutorCorePoolSize = 1;
    private final ThreadFactory virtualThreadFactory = configVirtualThreadFactory();

    public int getSchedulerPoolSize() {
        return this.schedulerPoolSize;
    }

    public int getAsyncExecutorCorePoolSize() {
        return this.asyncExecutorCorePoolSize;
    }

    public int getHttpExecutorCorePoolSize() {
        return this.httpExecutorCorePoolSize;
    }

    public int getServiceExecutorCorePoolSize() {
        return this.serviceExecutorCorePoolSize;
    }

    public int getIndexExecutorCorePoolSize() {
        return this.indexExecutorCorePoolSize;
    }

    public ThreadFactory getVirtualThreadFactory() {
        return this.virtualThreadFactory;
    }

    public void setSchedulerPoolSize(int schedulerPoolSize) {
        this.schedulerPoolSize = schedulerPoolSize;
    }

    public void setAsyncExecutorCorePoolSize(int asyncExecutorCorePoolSize) {
        this.asyncExecutorCorePoolSize = asyncExecutorCorePoolSize;
    }

    public void setHttpExecutorCorePoolSize(int httpExecutorCorePoolSize) {
        this.httpExecutorCorePoolSize = httpExecutorCorePoolSize;
    }

    public void setServiceExecutorCorePoolSize(int serviceExecutorCorePoolSize) {
        this.serviceExecutorCorePoolSize = serviceExecutorCorePoolSize;
    }

    public void setIndexExecutorCorePoolSize(int indexExecutorCorePoolSize) {
        this.indexExecutorCorePoolSize = indexExecutorCorePoolSize;
    }

    private ThreadFactory configVirtualThreadFactory() {
        return Thread.ofVirtual()
                .name("ApplicationVirtualThreadFactory")
                .inheritInheritableThreadLocals(true)
                .uncaughtExceptionHandler(new InternalExceptionHandler())
                .factory();
    }

    private static class InternalExceptionHandler implements Thread.UncaughtExceptionHandler {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.error("Exception in thread [{}]", t.getName(), e);
        }
    }
}
