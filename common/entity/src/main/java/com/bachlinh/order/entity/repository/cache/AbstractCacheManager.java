package com.bachlinh.order.entity.repository.cache;

import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.repository.query.CacheableQuery;
import com.bachlinh.order.entity.utils.QueryUtils;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractCacheManager<T> implements CacheManager<T>, CacheReader<String, Collection<AbstractEntity<?>>>, CacheWriter<CacheableQuery> {

    private final T cacheManager;
    private final CacheAllocator allocator;
    private final CacheLoader cacheLoader;
    private final CacheDestroyer cacheDestroyer;

    protected AbstractCacheManager(T cacheManager, CacheAllocator allocator, CacheLoader cacheLoader, CacheDestroyer cacheDestroyer) {
        this.cacheManager = cacheManager;
        this.allocator = allocator;
        this.cacheLoader = cacheLoader;
        this.cacheDestroyer = cacheDestroyer;
    }

    @Override
    public T unwrap() {
        return cacheManager;
    }

    @Override
    public Cache createCache(String alias) {
        return allocator.allocate(this, alias);
    }

    @Override
    public Cache getCache(String alias) {
        Cache cache = cacheLoader.load(alias);
        if (cache == null) {
            cache = createCache(alias);
        }
        return cache;
    }

    @Override
    public void write(CacheableQuery key, Object value) {
        Map<String, Object> attributes = key.attributes();
        String query = key.nativeQuery();
        query = QueryUtils.bindAttributes(query, attributes);
        internalWrite(query, value);
    }

    @Override
    public void removeCache(String alias) {
        cacheDestroyer.destroy(alias);
    }

    protected abstract void internalWrite(String key, Object value);
}
