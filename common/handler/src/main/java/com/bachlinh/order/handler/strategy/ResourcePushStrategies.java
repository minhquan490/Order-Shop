package com.bachlinh.order.handler.strategy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.environment.Environment;

public interface ResourcePushStrategies {
    void pushResource(NativeResponse<?> response, HttpServletRequest request);

    static ResourcePushStrategies getSyncPushStrategies(Environment environment) {
        return DefaultResourcePushStrategies.getInstance(environment);
    }

    static ResourcePushStrategies getAsyncPushStrategies(Environment environment, ThreadPoolTaskExecutor executor) {
        return new AyncResourcePushStrategies(environment, executor);
    }
}
