package com.bachlinh.order.handler.interceptor;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;

public interface WebInterceptorChain extends ObjectInterceptor {

    void registerInterceptor(WebInterceptor interceptor);

    static WebInterceptorChain getDefault(DependenciesResolver resolver, Environment environment) {
        return new DefaultWebInterceptorChain(resolver, environment);
    }
}
