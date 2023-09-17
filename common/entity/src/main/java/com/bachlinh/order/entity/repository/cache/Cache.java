package com.bachlinh.order.entity.repository.cache;

import java.util.Map;
import java.util.Set;

public interface Cache {
    <T, U> CacheValue<U> get(CacheKey<T> key);

    <T, U> void put(CacheKey<T> key, CacheValue<U> value);

    <T> boolean containKey(CacheKey<T> key);

    <T> void remove(CacheKey<T> key);

    <T, U> Map<CacheKey<T>, CacheValue<U>> getAll(Set<CacheKey<T>> keys);

    <T, U> void putAll(Map<CacheKey<T>, CacheValue<U>> entries);

    <T> void removeAll(Set<CacheKey<T>> keys);

    String getName();

    void clear();
}
