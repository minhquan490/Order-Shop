package com.bachlinh.order.entity.repository.query;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public final class CacheableQuery {
    private final String nativeQuery;
    private final Map<String, Object> attributes;
}
