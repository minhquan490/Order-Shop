package com.bachlinh.order.handler.interceptor.internal;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.interceptor.spi.AbstractInterceptor;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptor;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DefaultWebInterceptorChain implements WebInterceptorChain {
    private final List<WebInterceptor> interceptors = new ArrayList<>();
    private final DependenciesResolver dependenciesResolver;
    private final Environment environment;

    public DefaultWebInterceptorChain(DependenciesResolver dependenciesResolver, Environment environment) {
        this.dependenciesResolver = dependenciesResolver;
        this.environment = environment;
    }

    @Override
    public boolean shouldHandle(NativeRequest<?> request, NativeResponse<?> response) {
        return interceptors.stream().allMatch(interceptor -> interceptor.preHandle(request, response));
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
