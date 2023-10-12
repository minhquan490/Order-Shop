package com.bachlinh.order.entity.repository.query;

import java.util.Map;

public record CacheableQuery(String nativeQuery, Map<String, Object> attributes) {
}
