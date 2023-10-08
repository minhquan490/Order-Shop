package com.bachlinh.order.core.concurrent;

public abstract class AbstractThreadPoolOption implements ThreadPoolOption {

    private int schedulerPoolSize = 1;
    private int asyncExecutorCorePoolSize = 1;
    private int httpExecutorCorePoolSize = 1;
    private int serviceExecutorCorePoolSize = 1;
    private int indexExecutorCorePoolSize = 1;

    @Override
    public int getSchedulerPoolSize() {
        return this.schedulerPoolSize;
    }

    @Override
    public int getAsyncExecutorCorePoolSize() {
        return this.asyncExecutorCorePoolSize;
    }

    @Override
    public int getHttpExecutorCorePoolSize() {
        return this.httpExecutorCorePoolSize;
    }

    @Override
    public int getServiceExecutorCorePoolSize() {
        return this.serviceExecutorCorePoolSize;
    }

    @Override
    public int getIndexExecutorCorePoolSize() {
        return this.indexExecutorCorePoolSize;
    }

    @Override
    public void setSchedulerPoolSize(int schedulerPoolSize) {
        this.schedulerPoolSize = schedulerPoolSize;
    }

    @Override
    public void setAsyncExecutorCorePoolSize(int asyncExecutorCorePoolSize) {
        this.asyncExecutorCorePoolSize = asyncExecutorCorePoolSize;
    }

    @Override
    public void setHttpExecutorCorePoolSize(int httpExecutorCorePoolSize) {
        this.httpExecutorCorePoolSize = httpExecutorCorePoolSize;
    }

    @Override
    public void setServiceExecutorCorePoolSize(int serviceExecutorCorePoolSize) {
        this.serviceExecutorCorePoolSize = serviceExecutorCorePoolSize;
    }

    @Override
    public void setIndexExecutorCorePoolSize(int indexExecutorCorePoolSize) {
        this.indexExecutorCorePoolSize = indexExecutorCorePoolSize;
    }
}
