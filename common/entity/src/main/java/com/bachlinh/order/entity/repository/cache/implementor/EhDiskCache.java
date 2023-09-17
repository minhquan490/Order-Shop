package com.bachlinh.order.entity.repository.cache.implementor;

import com.bachlinh.order.entity.repository.cache.AbstractCache;
import com.bachlinh.order.entity.repository.cache.CacheKey;
import com.bachlinh.order.entity.repository.cache.CacheValue;
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

class EhDiskCache extends AbstractCache {

    private static final CacheConfigurationBuilder<Object, Object> SHARED_CONFIG;

    static {
        ResourcePools resourcePools = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .disk(2, MemoryUnit.GB, true)
                .build();
        SHARED_CONFIG = CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, resourcePools)
                .withKeySerializer(new DiskCacheKeySerializer())
                .withValueSerializer(new DiskCacheValueSerializer())
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.of(1, ChronoUnit.HOURS)));
    }

    public EhDiskCache(String name, CacheManager cacheManager) {
        super(name, cacheManager);
    }

    @Override
    protected <U> CacheValue<U> wrap(U value) {
        return new EhCacheValue<>(value);
    }

    @Override
    protected <T, U> Map<CacheKey<T>, CacheValue<U>> processReturnData(Map<T, U> returnResults) {
        return returnResults.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> new EhCacheKey<>(entry.getKey()), entry -> new EhCacheValue<>(entry.getValue())));
    }

    @Override
    protected CacheConfigurationBuilder<Object, Object> getConfigurationBuilder() {
        return SHARED_CONFIG;
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
