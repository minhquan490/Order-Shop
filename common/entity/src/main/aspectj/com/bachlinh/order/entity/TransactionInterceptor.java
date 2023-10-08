package com.bachlinh.order.entity;

import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.container.AbstractDependenciesResolver;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.utils.RequestHelper;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.entity.transaction.spi.TransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public final class TransactionInterceptor {

    private TransactionManager<?> transactionManager;
    private RepositoryManager repositoryManager;

    @Around("@annotation(com.bachlinh.order.core.annotation.Transactional)")
    @SuppressWarnings({"rawtypes"})
    public Object intercept(ProceedingJoinPoint joinPoint) {
        Transactional transactional = getTransactionalAnnotation(joinPoint);
        NativeRequest<?> currentRequest = RequestHelper.getCurrentRequest();
        TransactionHolder transactionHolder = startAndAssignTransaction(currentRequest, transactional);
        try {
            Object retVal = joinPoint.proceed();
            commit(transactionHolder);
            return retVal;
        } catch (Throwable cause) {
            doOnException(transactionHolder);
            throw new CriticalException(cause);
        } finally {
            RequestHelper.release();
            cleanupTransaction(transactionHolder);
        }
    }

    private TransactionHolder<?> startAndAssignTransaction(NativeRequest<?> request, Transactional transactional) {
        if (request.getRequestMethod().equals(RequestMethod.GET) || request.getRequestMethod().equals(RequestMethod.OPTION)) {
            return new NoOpTransactionHolder();
        }
        TransactionHolder<?> transaction = getTransactionManager().beginTransaction(transactional.isolation(), transactional.timeOut());
        getRepositoryManager().assignTransaction(transaction);
        return transaction;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void doOnException(TransactionHolder transaction) {
        if (transaction instanceof NoOpTransactionHolder) {
            return;
        }
        getTransactionManager().rollback(transaction);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void commit(TransactionHolder transaction) {
        if (!(transaction instanceof NoOpTransactionHolder)) {
            transactionManager.commit(transaction);
        }
    }

    private void cleanupTransaction(TransactionHolder<?> transaction) {
        if (transaction instanceof NoOpTransactionHolder) {
            return;
        }
        getRepositoryManager().releaseTransaction(transaction);
    }

    private RepositoryManager getRepositoryManager() {
        if (repositoryManager == null) {
            repositoryManager = AbstractDependenciesResolver.getSelf().resolveDependencies(RepositoryManager.class);
        }
        return repositoryManager;
    }

    private TransactionManager<?> getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = AbstractDependenciesResolver.getSelf().resolveDependencies(TransactionManager.class);
        }
        return transactionManager;
    }

    private Transactional getTransactionalAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.getDeclaredAnnotation(Transactional.class);
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
