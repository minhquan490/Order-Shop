package com.bachlinh.order.web.common.interceptor;

import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.interceptor.internal.DefaultWebInterceptorChain;
import com.bachlinh.order.handler.interceptor.spi.AbstractInterceptor;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptor;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.UnsafeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                    AbstractInterceptor abstractInterceptor;
                    try {
                        abstractInterceptor = (AbstractInterceptor) UnsafeUtils.allocateInstance(interceptorClass);
                    } catch (InstantiationException e) {
                        logger.error("Can not init interceptor [{}]", interceptorClass.getName(), e);
                        return null;
                    }
                    return abstractInterceptor.getInstance();
                })
                .filter(Objects::nonNull)
                .toList();
        var chain = new DefaultWebInterceptorChain(resolver, environment);
        interceptors.forEach(chain::registerInterceptor);
        return chain;
    }
}
