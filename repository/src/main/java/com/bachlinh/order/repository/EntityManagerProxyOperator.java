package com.bachlinh.order.repository;

import jakarta.persistence.EntityManager;

public interface EntityManagerProxyOperator {
    void assignEntityManager(EntityManager otherEntityManager);

    void releaseEntityManager();
}
