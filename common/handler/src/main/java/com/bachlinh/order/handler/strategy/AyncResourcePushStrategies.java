package com.bachlinh.order.handler.strategy;

import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.environment.Environment;
import jakarta.servlet.http.HttpServletRequest;

class AyncResourcePushStrategies implements ResourcePushStrategies {
    private final ThreadPoolManager threadPoolManager;
    private final DefaultResourcePushStrategies defaultResourcePushStrategies;

    AyncResourcePushStrategies(Environment environment, ThreadPoolManager threadPoolManager) {
        this.threadPoolManager = threadPoolManager;
        this.defaultResourcePushStrategies = DefaultResourcePushStrategies.getInstance(environment);
    }


    @Override
    public void pushResource(NativeResponse<?> response, HttpServletRequest request) {
        Runnable task = () -> defaultResourcePushStrategies.pushResource(response, request);
        threadPoolManager.execute(task, RunnableType.HTTP);
    }
}
