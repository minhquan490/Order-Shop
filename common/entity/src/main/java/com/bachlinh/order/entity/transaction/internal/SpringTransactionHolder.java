package com.bachlinh.order.entity.transaction.internal;

import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.entity.utils.TransactionUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.Objects;

class SpringTransactionHolder implements TransactionHolder<TransactionStatus> {
    private static final int DEFAULT_TIME_OUT = 60;

    private final PlatformTransactionManager transactionManager;

    private TransactionStatus transactionStatus;

    SpringTransactionHolder(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionStatus getTransaction() {
        return startTransaction(null, null);
    }

    @Override
    public TransactionStatus getTransaction(Isolation isolation) {
        return startTransaction(isolation, null);
    }

    @Override
    public TransactionStatus getTransaction(Isolation isolation, int timeOut) {
        return startTransaction(isolation, timeOut);
    }

    @Override
    public boolean isActive() {
        return transactionStatus != null;
    }

    @Override
    public void cleanup(TransactionHolder<?> holder) {
        EntityManager entityManager = TransactionUtils.extractEntityManager(holder);
        if (entityManager != null) {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            if (entityTransaction.isActive()) {
                entityTransaction.commit();
            }
            entityManager.close();
        }
    }

    private TransactionStatus startTransaction(Isolation isolation, Integer timeOut) {
        TransactionDefinition transactionDefinition;
        if (isolation == null) {
            transactionDefinition = new DefaultTransactionDefinition();
        } else {
            transactionDefinition = new Definition(Objects.requireNonNullElse(timeOut, DEFAULT_TIME_OUT), isolation);
        }

        if (this.transactionStatus == null) {
            this.transactionStatus = transactionManager.getTransaction(transactionDefinition);
        }

        return this.transactionStatus;
    }

    private static class DefaultTransactionDefinition implements TransactionDefinition {

        @Override
        public int getTimeout() {
            return SpringTransactionHolder.DEFAULT_TIME_OUT;
        }

        @Override
        public int getIsolationLevel() {
            return Isolation.DEFAULT.getLevel();
        }

    }

    private static class Definition implements TransactionDefinition {

        private final int timeOut;
        private final Isolation isolation;

        Definition(int timeOut, Isolation isolationLevel) {
            this.timeOut = timeOut;
            this.isolation = isolationLevel;
        }

        @Override
        public int getTimeout() {
            return this.timeOut;
        }

        @Override
        public int getIsolationLevel() {
            return this.isolation.getLevel();
        }
    }
}
