package com.bachlinh.order.repository.cache;

import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.query.CacheableQuery;
import com.bachlinh.order.repository.utils.QueryUtils;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractCacheManager<T> implements CacheManager<T>, CacheReader<String, Collection<AbstractEntity<?>>>, CacheWriter<CacheableQuery> {

    private final T cacheManager;
    private final CacheAllocator allocator;
    private final CacheLoader cacheLoader;
    private final CacheDestroyer cacheDestroyer;

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
        Map<String, Object> attributes = key.getAttributes();
        String query = key.getNativeQuery();
        query = QueryUtils.bindAttributes(query, attributes);
        internalWrite(query, value);
    }

    @Override
    public void removeCache(String alias) {
        cacheDestroyer.destroy(alias);
    }

    protected abstract void internalWrite(String key, Object value);
}
