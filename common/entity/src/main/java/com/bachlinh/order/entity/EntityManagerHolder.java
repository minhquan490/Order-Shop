package com.bachlinh.order.entity;

import jakarta.persistence.EntityManager;

public interface EntityManagerHolder {

    EntityManager getEntityManager();
}
