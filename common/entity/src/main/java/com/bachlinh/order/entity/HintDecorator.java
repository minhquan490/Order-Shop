package com.bachlinh.order.entity;

import jakarta.persistence.TypedQuery;

/**
 * The hint decorator for decorate query hint before generate query.
 *
 * @author Hoang Minh Quan
 */
public interface HintDecorator {

    /**
     * Apply cache to query before fetching database.
     *
     * @param query  Query for hint apply to.
     * @param region Cache storage name.
     */
    void applyCacheQueryHints(TypedQuery<?> query, String region);
}
