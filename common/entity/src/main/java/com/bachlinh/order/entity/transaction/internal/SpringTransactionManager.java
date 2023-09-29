package com.bachlinh.order.entity.transaction.internal;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.entity.transaction.spi.TransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class SpringTransactionManager implements TransactionManager<TransactionStatus> {

    private final PlatformTransactionManager transactionManager;

    public SpringTransactionManager(DependenciesResolver resolver) {
        this.transactionManager = resolver.resolveDependencies(PlatformTransactionManager.class);
    }

    @Override
    public TransactionHolder<TransactionStatus> beginTransaction() {
        return new SpringTransactionHolder(transactionManager);
    }

    @Override
    public void commit(TransactionHolder<TransactionStatus> holder) {
        TransactionStatus transaction = holder.getTransaction();
        if (transaction != null) {
            transactionManager.commit(transaction);
        }
    }

    @Override
    public void rollback(TransactionHolder<TransactionStatus> holder) {
        TransactionStatus transaction = holder.getTransaction();
        if (transaction != null) {
            transactionManager.rollback(transaction);
        }
    }
}
