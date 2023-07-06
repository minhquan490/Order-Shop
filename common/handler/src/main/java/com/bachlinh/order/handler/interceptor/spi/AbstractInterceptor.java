package com.bachlinh.order.handler.interceptor.spi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;

@Getter(AccessLevel.PROTECTED)
@Setter
public abstract non-sealed class AbstractInterceptor implements WebInterceptor {
    private DependenciesResolver resolver;
    private Environment environment;

    @Override
    public boolean preHandle(NativeRequest<?> request, NativeResponse<?> response) {
        return true;
    }

    @Override
    public void postHandle(NativeRequest<?> request, NativeResponse<?> response) {
        // Do nothing in abstract
    }

    @Override
    public void onComplete(NativeRequest<?> request, NativeResponse<?> response) {
        // Do nothing in abstract
    }
}
