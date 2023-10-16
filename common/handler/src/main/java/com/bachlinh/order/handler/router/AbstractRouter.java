package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.handler.ExceptionReturn;
import com.bachlinh.order.http.handler.Router;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.transaction.spi.EntityTransactionManager;
import com.bachlinh.order.handler.controller.Controller;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;
import com.bachlinh.order.handler.strategy.ResponseStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractRouter<T, U> implements Router<T, U> {
    private final DependenciesResolver resolver;
    private final Node rootNode;
    private final WebInterceptorChain webInterceptorChain;
    private final EntityFactory entityFactory;

    protected AbstractRouter(DependenciesResolver resolver) {
        this.resolver = resolver;
        this.rootNode = configRootNode();
        this.webInterceptorChain = resolver.resolveDependencies(WebInterceptorChain.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
    }

    @Override
    public final void handleRequest(T request, U response) {
        AtomicReference<NativeRequest<?>> requestReference = new AtomicReference<>(registerReq(request));
        AtomicReference<NativeResponse<?>> responseReference = new AtomicReference<>(createDefault());
        try {
            preHandle(requestReference, responseReference, response);
            if (webInterceptorChain.shouldHandle(requestReference.get(), responseReference.get())) {
                responseReference.set(internalHandle(requestReference.get()));
            } else {
                responseReference.set(createUnPassedInterceptorResponse(responseReference.get()));
            }
            postHandle(requestReference, responseReference, response);
            writeResponse(responseReference.get(), response);
        } catch (Throwable throwable) {
            onErrorBeforeHandle(throwable, response);
        } finally {
            onComplete(requestReference, responseReference);
            release(request, requestReference, responseReference);
        }
    }

    protected abstract NativeResponse<?> internalHandle(NativeRequest<?> request);

    @NonNull
    protected abstract NativeRequest<?> registerReq(T request);

    protected abstract NativeResponse<?> createDefault();

    protected abstract ResponseStrategy<U> getStrategy();

    protected abstract void writeResponse(NativeResponse<?> nativeResponse, U response);

    protected abstract void onErrorBeforeHandle(Throwable throwable, U response);

    protected abstract void configResponse(NativeResponse<?> nativeResponse, U actualResponse);

    protected abstract void cleanUpRequest(T actualRequest, NativeRequest<?> transferredRequest);

    private Node configRootNode() {
        var factory = new NodeFactory(resolver);
        var paths = resolver.resolveDependencies(ControllerManager.class)
                .getContext()
                .queryAll()
                .stream()
                .map(Controller::getPath)
                .toList();
        return factory.createNode(paths);
    }

    /**
     * Unknown reason make request can not pass interceptor. Create default one
     */
    private NativeResponse<?> createUnPassedInterceptorResponse(NativeResponse<?> defaultResponse) {
        Object responseData = defaultResponse.getBody();
        int status = HttpStatus.UNAUTHORIZED.value();
        if (responseData == null) {
            String[] messages = new String[]{"Fail to process request, you need to login for continue"};
            responseData = new ExceptionReturn(status, messages);
        }
        return NativeResponse.builder()
                .statusCode(status)
                .headers(defaultResponse.getHeaders())
                .body(responseData)
                .build();
    }

    protected DependenciesResolver getResolver() {
        return this.resolver;
    }

    protected Node getRootNode() {
        return this.rootNode;
    }

    protected EntityFactory getEntityFactory() {
        return this.entityFactory;
    }

    private void postHandle(AtomicReference<NativeRequest<?>> requestReference, AtomicReference<NativeResponse<?>> responseReference, U response) {
        webInterceptorChain.afterHandle(requestReference.get(), responseReference.get());
        getStrategy().apply(responseReference.get(), response);
    }

    private void preHandle(AtomicReference<NativeRequest<?>> requestReference, AtomicReference<NativeResponse<?>> responseReference, U response) {
        configResponse(responseReference.get(), response);
        rootNode.setNativeRequest(requestReference.get());
        rootNode.setNativeResponse(responseReference.get());
    }

    private void release(T request, AtomicReference<NativeRequest<?>> requestReference, AtomicReference<NativeResponse<?>> responseReference) {
        EntityTransactionManager<?> entityTransactionManager = getEntityFactory().getTransactionManager();
        if (entityTransactionManager.hasSavePoint()) {
            entityTransactionManager.release();
        }
        getRootNode().release();
        cleanUpRequest(request, requestReference.get());
        requestReference.set(null);
        responseReference.set(null);
    }

    private void onComplete(AtomicReference<NativeRequest<?>> requestReference, AtomicReference<NativeResponse<?>> responseReference) {
        if (requestReference.get() != null && responseReference.get() != null) {
            webInterceptorChain.onCompletion(requestReference.get(), responseReference.get());
        }
    }
}
