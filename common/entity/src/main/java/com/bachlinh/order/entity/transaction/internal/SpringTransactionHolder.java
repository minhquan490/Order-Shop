package com.bachlinh.order.entity.transaction.internal;

import com.bachlinh.order.entity.transaction.shaded.JpaTransactionManager;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.sql.Connection;

class SpringTransactionHolder implements TransactionHolder<TransactionStatus> {

    private final PlatformTransactionManager transactionManager;

    SpringTransactionHolder(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionStatus getTransaction() {
        return transactionManager.getTransaction(new SpringTransactionDefinition());
    }

    @Override
    public void cleanup(Object transaction) {
        DefaultTransactionStatus defaultTransactionStatus = (DefaultTransactionStatus) transaction;
        JpaTransactionManager.JpaTransactionObject transactionObject = (JpaTransactionManager.JpaTransactionObject) defaultTransactionStatus.getTransaction();
        transactionObject.getEntityManagerHolder().getEntityManager().close();
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

        @Override
        public int getPropagationBehavior() {
            return PROPAGATION_REQUIRES_NEW;
        }
    }
}
