package com.bachlinh.order.core.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error("Exception in thread [{}]", t.getName(), e);
    }
}
