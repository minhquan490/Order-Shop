package com.bachlinh.order.core.concurrent;

import java.util.concurrent.ThreadFactory;

class VirtualThreadPoolOption extends AbstractThreadPoolOption {
    private final ThreadFactory virtualThreadFactory = configVirtualThreadFactory();

    @Override
    public ThreadFactory getThreadFactory() {
        return virtualThreadFactory;
    }

    private ThreadFactory configVirtualThreadFactory() {
        return Thread.ofVirtual()
                .name("virtual-thread")
                .inheritInheritableThreadLocals(false)
                .uncaughtExceptionHandler(new LoggingThreadExceptionHandler())
                .factory();
    }
}
