package com.bachlinh.order.entity.repository.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCacheContext implements CacheAllocator, CacheLoader, CacheDestroyer {

    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    @Override
    public final Cache allocate(CacheManager<?> cacheManager, String alias) {
        Cache cache = doCreateCache(alias, (org.ehcache.CacheManager) cacheManager.unwrap());
        cacheMap.put(alias, cache);
        return cache;
    }

    @Override
    public final void destroy(String alias) {
        cacheMap.remove(alias);
    }

    @Override
    public final Cache load(String alias) {
        return cacheMap.get(alias);
    }

    protected abstract Cache doCreateCache(String alias, org.ehcache.CacheManager cacheManager);
}
