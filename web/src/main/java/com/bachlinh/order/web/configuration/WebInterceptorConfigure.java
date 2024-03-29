package com.bachlinh.order.web.configuration;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.handler.interceptor.WebInterceptor;
import com.bachlinh.order.handler.interceptor.WebInterceptorChain;
import com.bachlinh.order.web.common.interceptor.InterceptorInitializer;

import java.util.Objects;

import static com.bachlinh.order.handler.interceptor.WebInterceptorChain.getDefault;

public abstract class WebInterceptorConfigure extends EntityModuleConfigure {

    private final ApplicationScanner scanner;

    protected WebInterceptorConfigure(ApplicationScanner scanner) {
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
        var chain = getDefault(resolver, environment);
        interceptors.forEach(chain::registerInterceptor);
        return chain;
    }
}
