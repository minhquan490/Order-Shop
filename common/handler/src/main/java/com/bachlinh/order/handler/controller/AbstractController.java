package com.bachlinh.order.handler.controller;

import com.bachlinh.order.core.NativeMethodHandleRequestMetadataReader;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Scope;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.container.AbstractDependenciesResolver;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.http.ValidationFailureException;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.http.NativeRequest;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.core.utils.map.LinkedMultiValueMap;
import com.bachlinh.order.core.utils.map.MultiValueMap;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.entity.transaction.spi.TransactionManager;
import com.bachlinh.order.handler.service.ServiceManager;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.RuleManager;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Scope(Scope.ControllerScope.REQUEST)
public abstract non-sealed class AbstractController<T, U> implements Controller<T, U> {
    private static final String STATUS_KEY = "status";

    private final NativeMethodHandleRequestMetadataReader reader;
    private final Method internalHandlerMethod;

    private NativeRequest<U> request;
    private NativeResponse<T> response;
    private DependenciesContainerResolver containerResolver;
    private ServiceManager serviceManager;
    private RepositoryManager repositoryManager;
    private RuleManager ruleManager;
    private TransactionManager<?> transactionManager;
    private Environment environment;
    private String name;

    protected AbstractController() {
        try {
            this.internalHandlerMethod = getClass().getDeclaredMethod(ControllerFactory.INTERNAL_HANDLER_METHOD_NAME, Payload.class);
            this.reader = NativeMethodHandleRequestMetadataReader.getInstance();
        } catch (NoSuchMethodException e) {
            throw new CriticalException("Can not read actual method handle that will handle request");
        }
    }

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
        T returnValue = callControllerHandle(request);
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

    protected abstract T internalHandler(Payload<U> request);

    public abstract AbstractController<T, U> newInstance();

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

    private NativeResponse<T> merge(ResponseEntity<T> other, Class<?> returnType) {
        var mergeTarget = createMergeTarget(other.getStatusCode().value(), other.getBody(), returnType, new LinkedMultiValueMap<>(other.getHeaders()));
        return merge(mergeTarget);
    }

    private NativeResponse<T> merge(Class<?> returnType, T returnValue) {
        int statusCode = getNativeResponse().getStatusCode();
        if (returnValue instanceof Map<?, ?> casted && casted.containsKey(STATUS_KEY)) {
            statusCode = (int) casted.get(STATUS_KEY);
        }
        var mergeTarget = createMergeTarget(statusCode, returnValue, returnType, new LinkedMultiValueMap<>());
        return merge(mergeTarget);
    }

    private NativeResponse<T> merge(NativeResponse<T> other) {
        return getNativeResponse().merge(other);
    }

    @SuppressWarnings("unchecked")
    private NativeResponse<T> createMergeTarget(int overrideStatus, Object retVal, Class<?> returnType, MultiValueMap<String, String> headers) {
        var builder = NativeResponse.builder();

        builder.activePushBuilder(getNativeResponse().isActivePushBuilder());
        builder.statusCode(overrideStatus <= 0 ? 200 : overrideStatus);
        builder.body(returnType.equals(Void.class) ? null : retVal);
        builder.headers(headers);

        return (NativeResponse<T>) builder.build();
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

    private Transactional getTransactionalAnnotation() {
        return internalHandlerMethod.getAnnotation(Transactional.class);
    }

    private T callControllerHandle(NativeRequest<U> request) {
        Transactional transactional = getTransactionalAnnotation();
        if (transactional != null) {
            TransactionHolder<?> holder = startAndAssignTransaction(request, transactional);
            try {
                T result = internalHandler(request.getBody());
                commit(holder);
                return result;
            } catch (RuntimeException e) {
                doOnException(holder);
                throw e;
            } finally {
                cleanupTransaction(holder);
            }
        } else {
            return internalHandler(request.getBody());
        }
    }

    private void cleanupTransaction(TransactionHolder<?> transaction) {
        if (transaction instanceof NoOpTransactionHolder) {
            return;
        }
        getRepositoryManager().releaseTransaction(transaction);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void commit(TransactionHolder transaction) {
        if (transaction instanceof NoOpTransactionHolder) {
            return;
        }
        getTransactionManager().commit(transaction);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void doOnException(TransactionHolder transaction) {
        if (transaction instanceof NoOpTransactionHolder) {
            return;
        }
        getTransactionManager().rollback(transaction);
    }

    private TransactionHolder<?> startAndAssignTransaction(NativeRequest<?> request, Transactional transactional) {
        if (request.getRequestMethod().equals(RequestMethod.GET) || request.getRequestMethod().equals(RequestMethod.OPTION)) {
            return new NoOpTransactionHolder();
        }
        TransactionHolder<?> transaction = getTransactionManager().beginTransaction(transactional.isolation(), transactional.timeOut());
        getRepositoryManager().assignTransaction(transaction);
        return transaction;
    }

    private TransactionManager<?> getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = AbstractDependenciesResolver.getSelf().resolveDependencies(TransactionManager.class);
        }
        return transactionManager;
    }

    private RepositoryManager getRepositoryManager() {
        if (repositoryManager == null) {
            repositoryManager = AbstractDependenciesResolver.getSelf().resolveDependencies(RepositoryManager.class);
        }
        return repositoryManager;
    }

    private static class NoOpTransactionHolder implements TransactionHolder<Object> {

        @Override
        public Object getTransaction() {
            return null;
        }

        @Override
        public Object getTransaction(Isolation isolation) {
            return null;
        }

        @Override
        public Object getTransaction(Isolation isolation, int timeOut) {
            return null;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void cleanup(TransactionHolder<?> holder) {
            // Do nothing
        }
    }
}
