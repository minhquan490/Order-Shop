package com.bachlinh.order.repository;

import java.util.List;
import java.util.Map;

public interface NativeQueryRepository {
    <K> List<K> executeNativeQuery(String query, Map<String, Object> attributes, Class<K> receiverType);
}