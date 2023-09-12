package com.bachlinh.order.repository.cache.implementor;

import com.bachlinh.order.repository.cache.AbstractCache;
import com.bachlinh.order.repository.cache.CacheKey;
import com.bachlinh.order.repository.cache.CacheValue;
import org.ehcache.CacheManager;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

public class EhHeapCache extends AbstractCache {
    public EhHeapCache(String name, CacheManager cacheManager) {
        super(name, cacheManager);
    }

    @Override
    protected <U> CacheValue<U> wrap(U value) {
        return () -> value;
    }

    @Override
    protected <T, U> Map<CacheKey<T>, CacheValue<U>> processReturnData(Map<T, U> returnResults) {
        return returnResults.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> new EhCacheKey<>(entry.getKey()), entry -> new EhCacheValue<>(entry.getValue())));
    }

    @Override
    protected CacheConfigurationBuilder<Object, Object> getConfigurationBuilder() {
        ResourcePools resourcePools = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(2, MemoryUnit.GB)
                .build();
        return CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, resourcePools)
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.of(1, ChronoUnit.HOURS)));
    }

    private static class EhCacheKey<T> implements CacheKey<T> {
        private final T value;

        EhCacheKey(T value) {
            this.value = value;
        }

        @Override
        public T unwrap() {
            return value;
        }
    }

    private static class EhCacheValue<U> implements CacheValue<U> {
        private final U value;

        EhCacheValue(U value) {
            this.value = value;
        }

        @Override
        public U unwrap() {
            return value;
        }
    }
}
