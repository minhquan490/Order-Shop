package com.bachlinh.order.entity.repository;

import com.bachlinh.order.core.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;

public interface RepositoryBase {
    RepositoryBase getInstance(DependenciesContainerResolver containerResolver);

    Class<?>[] getRepositoryTypes();

    void setEntityManager(EntityManager entityManager);
}
