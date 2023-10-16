package com.bachlinh.order.handler.interceptor.internal;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.handler.interceptor.spi.AbstractInterceptor;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptor;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DefaultWebInterceptorChain implements WebInterceptorChain {

    private final List<WebInterceptor> interceptors = new ArrayList<>();
    private final DependenciesResolver dependenciesResolver;
    private final Environment environment;
    private boolean calledInit = false;

    public DefaultWebInterceptorChain(DependenciesResolver dependenciesResolver, Environment environment) {
        this.dependenciesResolver = dependenciesResolver;
        this.environment = environment;
    }

    @Override
    public boolean shouldHandle(NativeRequest<?> request, NativeResponse<?> response) {
        boolean result = interceptors.stream()
                .filter(WebInterceptor::isEnable)
                .allMatch(interceptor -> {
                    AbstractInterceptor abstractInterceptor = (AbstractInterceptor) interceptor;
                    if (!calledInit) {
                        abstractInterceptor.init();
                    }
                    return abstractInterceptor.preHandle(request, response);
                });
        if (!calledInit) {
            calledInit = true;
        }
        return result;
    }

    @Override
    public void afterHandle(NativeRequest<?> request, NativeResponse<?> response) {
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).postHandle(request, response);
        }
    }

    @Override
    public void onCompletion(NativeRequest<?> request, NativeResponse<?> response) {
        interceptors.forEach(interceptor -> interceptor.onComplete(request, response));
    }

    @Override
    public synchronized void registerInterceptor(WebInterceptor interceptor) {
        var abstractInterceptor = (AbstractInterceptor) interceptor;
        abstractInterceptor.setEnvironment(this.environment);
        abstractInterceptor.setResolver(this.dependenciesResolver);
        this.interceptors.add(abstractInterceptor);
        interceptors.sort(Comparator.comparing(WebInterceptor::getOrder));
    }
}
