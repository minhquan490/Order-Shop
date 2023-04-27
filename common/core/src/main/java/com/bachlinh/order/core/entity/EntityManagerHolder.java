package com.bachlinh.order.core.entity;

import jakarta.persistence.EntityManager;

public interface EntityManagerHolder {

    EntityManager getEntityManager();
}
