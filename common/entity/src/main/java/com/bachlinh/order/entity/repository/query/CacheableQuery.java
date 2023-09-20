package com.bachlinh.order.entity.repository.query;

import java.util.Map;

public final class CacheableQuery {
    private final String nativeQuery;
    private final Map<String, Object> attributes;

    public CacheableQuery(String nativeQuery, Map<String, Object> attributes) {
        this.nativeQuery = nativeQuery;
        this.attributes = attributes;
    }

    public String getNativeQuery() {
        return this.nativeQuery;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
