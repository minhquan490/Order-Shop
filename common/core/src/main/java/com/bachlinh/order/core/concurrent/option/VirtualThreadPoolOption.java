package com.bachlinh.order.core.concurrent.option;

import java.util.concurrent.ThreadFactory;

import com.bachlinh.order.core.concurrent.LoggingThreadExceptionHandler;

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
