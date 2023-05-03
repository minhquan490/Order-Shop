package com.bachlinh.order.handler.strategy;

import jakarta.servlet.http.HttpServletRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.environment.Environment;

import java.util.concurrent.Executor;

class AyncResourcePushStrategies implements ResourcePushStrategies {
    private final Executor executor;
    private final DefaultResourcePushStrategies defaultResourcePushStrategies;

    AyncResourcePushStrategies(Environment environment, Executor executor) {
        this.executor = executor;
        this.defaultResourcePushStrategies = DefaultResourcePushStrategies.getInstance(environment);
    }


    @Override
    public void pushResource(NativeResponse<?> response, HttpServletRequest request) {
        Runnable task = () -> defaultResourcePushStrategies.pushResource(response, request);
        executor.execute(task);
    }
}
