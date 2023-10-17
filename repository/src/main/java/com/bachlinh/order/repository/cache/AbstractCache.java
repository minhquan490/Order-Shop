package com.bachlinh.order.repository.cache;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractCache implements Cache {
    private final String name;
    private final org.ehcache.Cache<Object, Object> internalCache;

    protected AbstractCache(String name, CacheManager cacheManager) {
        this.name = name;
        this.internalCache = cacheManager.createCache(name, getConfigurationBuilder());
    }

    @Override
    public final <T, U> CacheValue<U> get(CacheKey<T> key) {
        T unwrapKey = key.unwrap();

        @SuppressWarnings("unchecked")
        U value = (U) internalCache.get(unwrapKey);

        return wrap(value);
    }

    @Override
    public <T, U> void put(CacheKey<T> key, CacheValue<U> value) {
        T unwrapKey = key.unwrap();
        U unwrapValue = value.unwrap();
        internalCache.put(unwrapKey, unwrapValue);
    }

    @Override
    public <T> boolean containKey(CacheKey<T> key) {
        T unwrapKey = key.unwrap();
        return internalCache.containsKey(unwrapKey);
    }

    @Override
    public <T> void remove(CacheKey<T> key) {
        T unwrapKey = key.unwrap();
        internalCache.remove(unwrapKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, U> Map<CacheKey<T>, CacheValue<U>> getAll(Set<CacheKey<T>> cacheKeys) {
        Set<T> unwrapKeys = cacheKeys.stream().map(CacheKey::unwrap).collect(Collectors.toSet());
        Map<T, U> results = (Map<T, U>) internalCache.getAll(unwrapKeys);
        return processReturnData(results);
    }

    @Override
    public <T, U> void putAll(Map<CacheKey<T>, CacheValue<U>> entries) {
        Map<T, U> unwrapEntries = entries.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().unwrap(), entry -> entry.getValue().unwrap()));
        internalCache.putAll(unwrapEntries);
    }

    @Override
    public <T> void removeAll(Set<CacheKey<T>> cacheKeys) {
        Set<T> unwrapKeys = cacheKeys.stream().map(CacheKey::unwrap).collect(Collectors.toSet());
        internalCache.removeAll(unwrapKeys);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void clear() {
        internalCache.clear();
    }

    protected abstract <U> CacheValue<U> wrap(U value);

    protected abstract <T, U> Map<CacheKey<T>, CacheValue<U>> processReturnData(Map<T, U> returnResults);

    protected abstract CacheConfigurationBuilder<Object, Object> getConfigurationBuilder();
}
