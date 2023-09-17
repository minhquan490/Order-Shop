package com.bachlinh.order.entity.repository;

import java.util.List;
import java.util.Map;

public interface NativeQueryRepository {
    <K> List<K> getResultList(String query, Map<String, Object> attributes, Class<K> receiverType);

    <K> List<K> getResultList(String query, Map<String, Object> attributes, Class<K> receiverType, boolean forceCache);

    <K> K getSingleResult(String query, Map<String, Object> attributes, Class<K> receiverType);

    <K> K getSingleResult(String query, Map<String, Object> attributes, Class<K> receiverType, boolean forceCache);
}