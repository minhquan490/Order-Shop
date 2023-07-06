package com.bachlinh.order.web.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.interceptor.internal.DefaultWebInterceptorChain;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptor;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class WebInterceptorConfigurer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ApplicationScanner scanner;

    public WebInterceptorConfigurer(ApplicationScanner scanner) {
        this.scanner = scanner;
    }

    public WebInterceptorChain configInterceptorChain(DependenciesResolver resolver, Environment environment) {
        var interceptors = scanner.findComponents()
                .stream()
                .filter(WebInterceptor.class::isAssignableFrom)
                .map(interceptorClass -> {
                    try {
                        @SuppressWarnings("unchecked")
                        Constructor<WebInterceptor> constructor = (Constructor<WebInterceptor>) interceptorClass.getDeclaredConstructor();
                        if (!Modifier.isPublic(constructor.getModifiers())) {
                            constructor.setAccessible(true);
                        }
                        return constructor.newInstance();
                    } catch (Exception e) {
                        logger.error("Fail to instance [{}]", interceptorClass.getName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        var chain = new DefaultWebInterceptorChain(resolver, environment);
        interceptors.forEach(chain::registerInterceptor);
        return chain;
    }
}
