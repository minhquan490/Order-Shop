package com.bachlinh.order.web.common.interceptor;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.handler.interceptor.internal.DefaultWebInterceptorChain;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptor;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;

import java.util.Objects;

public class WebInterceptorConfigurer {

    private final ApplicationScanner scanner;

    public WebInterceptorConfigurer(ApplicationScanner scanner) {
        this.scanner = scanner;
    }

    public WebInterceptorChain configInterceptorChain(DependenciesResolver resolver, Environment environment) {
        InterceptorInitializer initializer = new InterceptorInitializer();
        var interceptors = scanner.findComponents()
                .stream()
                .filter(WebInterceptor.class::isAssignableFrom)
                .map(initializer::getObject)
                .filter(Objects::nonNull)
                .toList();
        var chain = new DefaultWebInterceptorChain(resolver, environment);
        interceptors.forEach(chain::registerInterceptor);
        return chain;
    }
}
