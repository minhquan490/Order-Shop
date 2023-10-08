package com.bachlinh.order.core.concurrent;

import java.util.concurrent.ThreadFactory;

class PlatformThreadPoolOption extends AbstractThreadPoolOption {

    private final ThreadFactory platformThreadFactory = configPlatformThreadFactory();

    @Override
    public ThreadFactory getThreadFactory() {
        return platformThreadFactory;
    }

    private ThreadFactory configPlatformThreadFactory() {
        return Thread.ofPlatform()
                .name("platform-thread")
                .inheritInheritableThreadLocals(false)
                .uncaughtExceptionHandler(new LoggingThreadExceptionHandler())
                .factory();
    }
}
