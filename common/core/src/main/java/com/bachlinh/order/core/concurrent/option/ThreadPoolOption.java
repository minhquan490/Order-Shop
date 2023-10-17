package com.bachlinh.order.core.concurrent.option;

import java.util.concurrent.ThreadFactory;

public interface ThreadPoolOption {
    int getSchedulerPoolSize();

    int getAsyncExecutorCorePoolSize();

    int getHttpExecutorCorePoolSize();

    int getServiceExecutorCorePoolSize();

    int getIndexExecutorCorePoolSize();

    void setSchedulerPoolSize(int schedulerPoolSize);

    void setAsyncExecutorCorePoolSize(int asyncExecutorCorePoolSize);

    void setHttpExecutorCorePoolSize(int httpExecutorCorePoolSize);

    void setServiceExecutorCorePoolSize(int serviceExecutorCorePoolSize);

    void setIndexExecutorCorePoolSize(int indexExecutorCorePoolSize);

    ThreadFactory getThreadFactory();

    static ThreadPoolOption ofVirtual() {
        return new VirtualThreadPoolOption();
    }

    static ThreadPoolOption ofPlatform() {
        return new PlatformThreadPoolOption();
    }
}
