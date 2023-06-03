package com.bachlinh.order.handler.router;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.converter.spi.ResponseConverter;
import com.bachlinh.order.core.http.handler.SpringServletHandler;
import com.bachlinh.order.entity.transaction.spi.EntityTransactionManager;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.handler.strategy.ResourcePushStrategies;
import com.bachlinh.order.handler.strategy.ServletResponseStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SimpleChildRoute extends AbstractChildRoute implements SpringServletHandler {
    private static final Map<Class<?>, ResponseConverter<?>> converters = new ConcurrentHashMap<>();

    private final String path;
    private final ChildRoute parent;
    private final String rootPath;
    private final ServletResponseStrategy responseStrategy = ServletResponseStrategy.defaultStrategy();

    private Boolean useAsyncPushStrategies;
    private ResourcePushStrategies resourcePushStrategies;

    public SimpleChildRoute(@Nullable ChildRoute parent, String path, @Nullable String rootPath) {
        this.path = path;
        this.parent = parent;
        this.rootPath = rootPath;
    }

    @Override
    public ChildRoute getParent() {
        return parent;
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public SpringServletHandler getServletHandler() {
        return this;
    }

    @Override
    public <T> ResponseEntity<T> handleServletRequest(String controllerPath, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        setNativeRequest(NativeRequest.buildNativeFromServletRequest(servletRequest));
        setNativeResponse(parseFrom(servletResponse));
        RequestMethod method = RequestMethod.valueOf(servletRequest.getMethod().toUpperCase());
        NativeResponse<T> nativeResponse = handleRequest(getNativeRequest(), controllerPath, method);
        responseStrategy.apply(nativeResponse, servletResponse);
        resolvePushResource(nativeResponse, servletRequest);
        return resolveBody(nativeResponse);
    }

    @Override
    public <T, U> NativeResponse<T> handleRequest(NativeRequest<U> request, String controllerPath, RequestMethod method) throws HttpRequestMethodNotSupportedException {
        String controllerUrl = parseChild(controllerPath);
        NativeResponse<T> response = getControllerManager().handleRequest(request, controllerUrl, method);
        EntityTransactionManager transactionManager = getEntityFactory().getTransactionManager();
        if (transactionManager.hasSavePoint()) {
            transactionManager.getSavePointManager().release();
        }
        return response;
    }

    private String parseChild(String path) {
        String root = getRootPath();
        if (root == null) {
            ChildRoute childRoute = getParent();
            return parseChild(childRoute.getPath().concat(path));
        } else {
            if (root.equals(path)) {
                return path;
            }
            return root.concat(path);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> NativeResponse<T> parseFrom(HttpServletResponse response) {
        if (!converters.containsKey(HttpServletResponse.class)) {
            converters.put(HttpServletResponse.class, ResponseConverter.servletResponseConverter());
        }
        ResponseConverter<HttpServletResponse> converter = (ResponseConverter<HttpServletResponse>) converters.get(HttpServletResponse.class);
        return (NativeResponse<T>) converter.convert(response);
    }

    private boolean isUseAsyncPushStrategies() {
        if (useAsyncPushStrategies == null) {
            useAsyncPushStrategies = Boolean.parseBoolean(getEnvironment().getProperty("server.push-strategies.async"));
        }
        return useAsyncPushStrategies;
    }

    private void resolvePushResource(NativeResponse<?> response, HttpServletRequest servletRequest) {
        if (response.isActivePushBuilder()) {
            if (resourcePushStrategies == null && !isUseAsyncPushStrategies()) {
                resourcePushStrategies = ResourcePushStrategies.getSyncPushStrategies(getEnvironment());
            }
            if (resourcePushStrategies == null && isUseAsyncPushStrategies()) {
                resourcePushStrategies = ResourcePushStrategies.getAsyncPushStrategies(getEnvironment(), getEntityFactory().getResolver().resolveDependencies(ThreadPoolTaskExecutor.class));
            }
            resourcePushStrategies.pushResource(response, servletRequest);
        }
    }

    private <T> ResponseEntity<T> resolveBody(NativeResponse<T> response) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatusCode());
        if (response.getBody() != null) {
            return builder.body(response.getBody());
        } else {
            return builder.build();
        }
    }
}
