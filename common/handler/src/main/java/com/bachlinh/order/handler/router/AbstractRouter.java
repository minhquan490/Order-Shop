package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.handler.ExceptionReturn;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.entity.transaction.spi.EntityTransactionManager;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.entity.transaction.spi.TransactionManager;
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
    private final TransactionManager<?> transactionManager;
    private final RepositoryManager repositoryManager;

    protected AbstractRouter(DependenciesResolver resolver) {
        this.resolver = resolver;
        this.rootNode = configRootNode();
        this.webInterceptorChain = resolver.resolveDependencies(WebInterceptorChain.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.transactionManager = resolver.resolveDependencies(TransactionManager.class);
        this.repositoryManager = resolver.resolveDependencies(RepositoryManager.class);
    }

    @Override
    public final void handleRequest(T request, U response) {
        AtomicReference<NativeRequest<?>> requestReference = new AtomicReference<>(registerReq(request));
        AtomicReference<NativeResponse<?>> responseReference = new AtomicReference<>(createDefault());
        TransactionHolder<?> transaction = startAndAssignTransaction(requestReference);
        try {
            preHandle(requestReference, responseReference, response);
            if (webInterceptorChain.shouldHandle(requestReference.get(), responseReference.get())) {
                responseReference.set(internalHandle(requestReference.get()));
            } else {
                responseReference.set(createUnPassedInterceptorResponse(responseReference.get()));
            }
            postHandle(transaction, requestReference, responseReference, response);
            writeResponse(responseReference.get(), response);
        } catch (Throwable throwable) {
            onErrorBeforeHandle(throwable, response);
            doOnException(transaction);
        } finally {
            onComplete(requestReference, responseReference);
            release(request, requestReference, responseReference, transaction);
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

    private TransactionHolder<?> startAndAssignTransaction(AtomicReference<NativeRequest<?>> requestReference) {
        NativeRequest<?> request = requestReference.get();
        if (request.getRequestMethod().equals(RequestMethod.GET)) {
            return new NoOpTransactionHolder();
        }
        TransactionHolder<?> transaction = transactionManager.beginTransaction();
        repositoryManager.assignTransaction(transaction);
        return transaction;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void postHandle(TransactionHolder<?> transaction, AtomicReference<NativeRequest<?>> requestReference, AtomicReference<NativeResponse<?>> responseReference, U response) {
        webInterceptorChain.afterHandle(requestReference.get(), responseReference.get());
        getStrategy().apply(responseReference.get(), response);
        if (!(transaction instanceof NoOpTransactionHolder)) {
            transactionManager.commit((TransactionHolder) transaction);
        }
    }

    private void preHandle(AtomicReference<NativeRequest<?>> requestReference, AtomicReference<NativeResponse<?>> responseReference, U response) {
        configResponse(responseReference.get(), response);
        rootNode.setNativeRequest(requestReference.get());
        rootNode.setNativeResponse(responseReference.get());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void doOnException(TransactionHolder<?> transaction) {
        if (transaction instanceof NoOpTransactionHolder) {
            return;
        }
        transactionManager.rollback((TransactionHolder) transaction);
    }

    private void cleanupTransaction(TransactionHolder<?> transaction) {
        if (transaction instanceof NoOpTransactionHolder) {
            return;
        }
        repositoryManager.releaseTransaction(transaction);
    }

    private void release(T request, AtomicReference<NativeRequest<?>> requestReference, AtomicReference<NativeResponse<?>> responseReference, TransactionHolder<?> transaction) {
        EntityTransactionManager entityTransactionManager = getEntityFactory().getTransactionManager();
        if (entityTransactionManager.hasSavePoint()) {
            entityTransactionManager.getSavePointManager().release();
        }
        getRootNode().release();
        cleanUpRequest(request, requestReference.get());
        requestReference.set(null);
        responseReference.set(null);
        cleanupTransaction(transaction);
    }

    private void onComplete(AtomicReference<NativeRequest<?>> requestReference, AtomicReference<NativeResponse<?>> responseReference) {
        if (requestReference.get() != null && responseReference.get() != null) {
            webInterceptorChain.onCompletion(requestReference.get(), responseReference.get());
        }
    }

    private static class NoOpTransactionHolder implements TransactionHolder<Object> {

        @Override
        public Object getTransaction() {
            return null;
        }

        @Override
        public void cleanup(Object transaction) {
            // Do nothing
        }
    }
}
