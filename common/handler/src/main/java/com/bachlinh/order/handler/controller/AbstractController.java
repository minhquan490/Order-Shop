package com.bachlinh.order.handler.controller;

import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.NativeMethodHandleRequestMetadataReader;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.http.ValidationFailureException;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.RuleManager;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public abstract non-sealed class AbstractController<T, U> implements Controller<T, U> {
    private final NativeMethodHandleRequestMetadataReader reader = NativeMethodHandleRequestMetadataReader.getInstance();
    private NativeRequest<U> request;
    private NativeResponse<T> response;

    private DependenciesContainerResolver containerResolver;

    private Environment environment;

    private RuleManager ruleManager;

    private String name;

    public boolean isController() {
        return this.getClass().isAnnotationPresent(RouteProvider.class);
    }

    public void setWrapper(ContainerWrapper wrapper, String profile) {
        this.containerResolver = DependenciesContainerResolver.buildResolver(wrapper.unwrap(), profile);
        this.environment = Environment.getInstance(profile);
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.getName().equals("internalHandler")) {
                NativeMethodHandleRequestMetadataReader.getInstance().defineMetadata(getPath(), method);
            }
        }
    }

    @Override
    public void setNativeRequest(NativeRequest<U> request) {
        this.request = request;
    }

    @Override
    public void setNativeResponse(NativeResponse<T> response) {
        this.response = response;
    }

    @Override
    public NativeRequest<U> getNativeRequest() {
        return request;
    }

    @Override
    public NativeResponse<T> getNativeResponse() {
        return response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final NativeResponse<T> handle(NativeRequest<U> request) {
        resolveValidator();
        inject();
        var metadata = reader.getNativeMethodMetadata(getPath());
        var paramType = metadata.parameterType();
        validateRequest(paramType, request.getBody().data());
        T returnValue = internalHandler(request.getBody());
        if (ResponseEntity.class.isAssignableFrom(returnValue.getClass())) {
            ResponseEntity<T> res = (ResponseEntity<T>) returnValue;
            return merge(res, paramType);
        }
        if (NativeResponse.class.isAssignableFrom(returnValue.getClass())) {
            return merge((NativeResponse<T>) returnValue);
        }
        return merge(metadata.returnTypes(), returnValue);
    }

    @Override
    public String getName() {
        if (name == null) {
            var provider = this.getClass().getAnnotation(RouteProvider.class);
            if (!StringUtils.hasText(provider.name())) {
                name = getClass().getSimpleName();
            } else {
                name = provider.name();
            }
        }
        return name;
    }

    public RuleManager getRuleManager() {
        if (ruleManager == null) {
            ruleManager = containerResolver.getDependenciesResolver().resolveDependencies(RuleManager.class);
        }
        return ruleManager;
    }

    public abstract AbstractController<T, U> newInstance();

    protected abstract T internalHandler(Payload<U> request);

    protected abstract void inject();

    private void resolveValidator() {
        var resolver = containerResolver.getDependenciesResolver();
        if (ruleManager == null) {
            ruleManager = resolver.resolveDependencies(RuleManager.class);
        }
    }

    private void validateRequest(Class<?> paramType, Object target) {
        if (ValidatedDto.class.isAssignableFrom(paramType)) {
            ValidatedDto dto = (ValidatedDto) target;
            var result = ruleManager.validate(dto);
            if (!result.shouldHandle()) {
                throw new ValidationFailureException(result.getErrorResult(), getPath());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private NativeResponse<T> merge(ResponseEntity<T> other, Class<?> paramType) {
        return getNativeResponse().merge((NativeResponse<T>) NativeResponse
                .builder()
                .statusCode(other.getStatusCode().value())
                .body(paramType.equals(Void.class) ? null : other.getBody())
                .headers(new LinkedMultiValueMap<>(other.getHeaders()))
                .build());
    }

    @SuppressWarnings("unchecked")
    private NativeResponse<T> merge(Class<?> returnType, T returnValue) {
        return getNativeResponse().merge((NativeResponse<T>) NativeResponse
                .builder()
                .activePushBuilder(getNativeResponse().isActivePushBuilder())
                .statusCode(getNativeResponse().getStatusCode() <= 0 ? 200 : getNativeResponse().getStatusCode())
                .body(returnType.equals(Void.class) ? null : returnValue)
                .build());
    }

    private NativeResponse<T> merge(NativeResponse<T> other) {
        return getNativeResponse().merge(other);
    }

    public DependenciesContainerResolver getContainerResolver() {
        return this.containerResolver;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setContainerResolver(DependenciesContainerResolver containerResolver) {
        this.containerResolver = containerResolver;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setRuleManager(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }
}
