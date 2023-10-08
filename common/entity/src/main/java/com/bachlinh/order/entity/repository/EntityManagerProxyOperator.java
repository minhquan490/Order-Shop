package com.bachlinh.order.entity.repository;

import jakarta.persistence.EntityManager;

public interface EntityManagerProxyOperator {
    void assignEntityManager(EntityManager otherEntityManager);

    void releaseEntityManager();
}
