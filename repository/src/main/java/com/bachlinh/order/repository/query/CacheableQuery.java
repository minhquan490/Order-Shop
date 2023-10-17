package com.bachlinh.order.repository.query;

import java.util.Map;

public record CacheableQuery(String nativeQuery, Map<String, Object> attributes) {
}
