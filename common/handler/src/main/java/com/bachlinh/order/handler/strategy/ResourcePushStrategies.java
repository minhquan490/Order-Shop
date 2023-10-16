package com.bachlinh.order.handler.strategy;

import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.core.environment.Environment;
import jakarta.servlet.http.HttpServletRequest;

public interface ResourcePushStrategies {
    void pushResource(NativeResponse<?> response, HttpServletRequest request);

    static ResourcePushStrategies getSyncPushStrategies(Environment environment) {
        return DefaultResourcePushStrategies.getInstance(environment);
    }

    static ResourcePushStrategies getAsyncPushStrategies(Environment environment, ThreadPoolManager threadPoolManager) {
        return new AyncResourcePushStrategies(environment, threadPoolManager);
    }
}
