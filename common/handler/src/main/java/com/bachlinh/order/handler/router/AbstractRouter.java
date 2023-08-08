package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.transaction.spi.EntityTransactionManager;
import com.bachlinh.order.handler.controller.Controller;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;
import com.bachlinh.order.handler.strategy.ResponseStrategy;
import com.bachlinh.order.service.container.DependenciesResolver;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
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
        NativeRequest<?> nativeReq = null;
        NativeResponse<?> nativeResp = null;
        try {
            nativeReq = registerReq(request);
            var defaultResp = createDefault();
            configResponse(defaultResp, response);
            rootNode.setNativeRequest(nativeReq);
            rootNode.setNativeResponse(defaultResp);
            if (webInterceptorChain.shouldHandle(nativeReq, defaultResp)) {
                nativeResp = internalHandle(nativeReq);
            } else {
                nativeResp = defaultResp;
            }
            webInterceptorChain.afterHandle(nativeReq, nativeResp);
            getStrategy().apply(nativeResp, response);
            writeResponse(nativeResp, response);
        } catch (Throwable throwable) {
            onErrorBeforeHandle(throwable, response);
        } finally {
            EntityTransactionManager transactionManager = getEntityFactory().getTransactionManager();
            if (transactionManager.hasSavePoint()) {
                transactionManager.getSavePointManager().release();
            }
            if (nativeReq != null && nativeResp != null) {
                webInterceptorChain.onCompletion(nativeReq, nativeResp);
            }
            if (nativeReq != null && nativeReq.isMultipart()) {
                nativeReq.cleanUp();
            }
        }
    }

    protected abstract NativeResponse<?> internalHandle(NativeRequest<?> request);

    protected abstract NativeRequest<?> registerReq(T request);

    protected abstract NativeResponse<?> createDefault();

    protected abstract ResponseStrategy<U> getStrategy();

    protected abstract void writeResponse(NativeResponse<?> nativeResponse, U response);

    protected abstract void onErrorBeforeHandle(Throwable throwable, U response);

    protected abstract void configResponse(NativeResponse<?> nativeResponse, U actualResponse);

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
}
