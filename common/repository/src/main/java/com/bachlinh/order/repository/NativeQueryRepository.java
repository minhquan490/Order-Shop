package com.bachlinh.order.repository;

import java.util.List;
import java.util.Map;

public interface NativeQueryRepository {
    <T> List<T> executeNativeQuery(String query, Map<String, Object> attributes, Class<T> receiverType);
}