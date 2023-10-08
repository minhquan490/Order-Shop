package com.bachlinh.order.entity.utils;

import com.bachlinh.order.entity.transaction.shaded.JpaTransactionManager;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public final class TransactionUtils {

    private TransactionUtils() {
    }

    public static EntityManager extractEntityManager(TransactionHolder<?> transactionHolder) {
        if (transactionHolder.getTransaction() instanceof DefaultTransactionStatus transactionStatus) {
            JpaTransactionManager.JpaTransactionObject transactionObject = (JpaTransactionManager.JpaTransactionObject) transactionStatus.getTransaction();
            return transactionObject.getEntityManagerHolder().getEntityManager();
        }
        return null;
    }
}
