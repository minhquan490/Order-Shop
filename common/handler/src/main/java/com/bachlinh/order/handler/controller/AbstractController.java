package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.NativeMethodHandleRequestMetadataReader;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.http.ValidationFailureException;
import com.bachlinh.order.handler.service.ServiceManager;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.RuleManager;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract non-sealed class AbstractController<T, U> implements Controller<T, U> {
    private static final String STATUS_KEY = "status";

    private final NativeMethodHandleRequestMetadataReader reader = NativeMethodHandleRequestMetadataReader.getInstance();
    private NativeRequest<U> request;
    private NativeResponse<T> response;

    private DependenciesContainerResolver containerResolver;
    private ServiceManager serviceManager;
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
    public final NativeResponse<T> handle(NativeRequest<U> request) {
        var metadata = reader.getNativeMethodMetadata(getPath());
        var paramType = metadata.parameterType();
        preHandle(paramType);
        T returnValue = internalHandler(request.getBody());
        return createResponse(returnValue, paramType, metadata);
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

    protected <K> K resolveService(Class<K> serviceType) {
        return this.serviceManager.getService(serviceType);
    }

    protected <K> K resolveDependencies(Class<K> dependenciesType) {
        return getContainerResolver().getDependenciesResolver().resolveDependencies(dependenciesType);
    }

    protected Map<String, Object> createDefaultResponse(int status, String[] messages) {
        Map<String, Object> resp = HashMap.newHashMap(2);
        resp.put(STATUS_KEY, status);
        resp.put("messages", messages);
        return resp;
    }

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private NativeResponse<T> merge(Class<?> returnType, T returnValue) {
        int statusCode = getNativeResponse().getStatusCode();
        if (returnValue instanceof Map casted && casted.containsKey(STATUS_KEY)) {
            statusCode = (int) casted.get(STATUS_KEY);
        }
        return getNativeResponse().merge((NativeResponse<T>) NativeResponse
                .builder()
                .activePushBuilder(getNativeResponse().isActivePushBuilder())
                .statusCode(statusCode <= 0 ? 200 : statusCode)
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

    private void preHandle(Class<?> paramType) {
        lazyInitServiceManager();
        resolveValidator();
        inject();
        validateRequest(paramType, request.getBody().data());
    }

    @SuppressWarnings("unchecked")
    private NativeResponse<T> createResponse(T returnValue, Class<?> paramType, NativeMethodHandleRequestMetadataReader.MetadataReader metadata) {
        if (ResponseEntity.class.isAssignableFrom(returnValue.getClass())) {
            ResponseEntity<T> res = (ResponseEntity<T>) returnValue;
            return merge(res, paramType);
        }
        if (NativeResponse.class.isAssignableFrom(returnValue.getClass())) {
            return merge((NativeResponse<T>) returnValue);
        }
        return merge(metadata.returnTypes(), returnValue);
    }

    private void lazyInitServiceManager() {
        if (this.serviceManager == null) {
            this.serviceManager = getContainerResolver().getDependenciesResolver().resolveDependencies(ServiceManager.class);
        }
    }
}
