package com.bachlinh.order.entity.transaction.internal;

import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.entity.utils.TransactionUtils;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.sql.Connection;

class SpringTransactionHolder implements TransactionHolder<TransactionStatus> {

    private final PlatformTransactionManager transactionManager;
    private boolean isActive = false;

    SpringTransactionHolder(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionStatus getTransaction() {
        this.isActive = true;
        return transactionManager.getTransaction(new SpringTransactionDefinition());
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void cleanup(TransactionHolder<?> holder) {
        EntityManager entityManager = TransactionUtils.extractEntityManager(holder);
        if (entityManager != null) {
            entityManager.close();
        }
    }

    private static class SpringTransactionDefinition implements TransactionDefinition {

        @Override
        public int getTimeout() {
            return 60;
        }

        @Override
        public int getIsolationLevel() {
            return Connection.TRANSACTION_READ_COMMITTED;
        }

    }
}
