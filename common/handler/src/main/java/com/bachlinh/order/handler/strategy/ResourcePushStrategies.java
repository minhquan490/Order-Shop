package com.bachlinh.order.handler.strategy;

import jakarta.servlet.http.HttpServletRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.environment.Environment;

import java.util.concurrent.Executor;

public interface ResourcePushStrategies {
    void pushResource(NativeResponse<?> response, HttpServletRequest request);

    static ResourcePushStrategies getSyncPushStrategies(Environment environment) {
        return DefaultResourcePushStrategies.getInstance(environment);
    }

    static ResourcePushStrategies getAsyncPushStrategies(Environment environment, Executor executor) {
        return new AyncResourcePushStrategies(environment, executor);
    }
}
