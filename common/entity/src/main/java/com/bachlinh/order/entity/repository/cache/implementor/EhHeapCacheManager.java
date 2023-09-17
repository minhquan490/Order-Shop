package com.bachlinh.order.entity.repository.cache.implementor;

import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.repository.cache.AbstractCacheManager;
import com.bachlinh.order.entity.repository.cache.Cache;
import com.bachlinh.order.entity.repository.cache.CacheAllocator;
import com.bachlinh.order.entity.repository.cache.CacheDestroyer;
import com.bachlinh.order.entity.repository.cache.CacheLoader;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EhHeapCacheManager extends AbstractCacheManager<CacheManager> {

    private static CacheManager cacheManager;
    private static final Context context;

    static {
        context = new Context();
    }

    public EhHeapCacheManager() {
        super(configCacheManager(), context, context, context);
        init();
    }

    @Override
    public void init() {
        cacheManager.init();
    }

    @Override
    public void close() {
        cacheManager.close();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<AbstractEntity<?>> readCache(String alias, String key) {
        Cache cache = context.load(alias);
        if (cache == null) {
            // Cache empty return must be null
            return null;
        }
        return (Collection<AbstractEntity<?>>) cache.get(() -> key).unwrap();
    }

    @Override
    protected void internalWrite(String key, Object value) {
        String alias = QueryUtils.parseQueryToEntitySimpleName(key);
        Cache cache = getCache(alias);
        cache.put(() -> key, () -> value);
    }

    private static CacheManager configCacheManager() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .build();
        return cacheManager;
    }

    private static class Context implements CacheAllocator, CacheLoader, CacheDestroyer {

        private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

        @Override
        public Cache allocate(com.bachlinh.order.entity.repository.cache.CacheManager<?> cacheManager, String alias) {
            Cache cache = new EhHeapCache(alias, (org.ehcache.CacheManager) cacheManager.unwrap());
            cacheMap.put(alias, cache);
            return cache;
        }

        @Override
        public Cache load(String alias) {
            return cacheMap.get(alias);
        }

        @Override
        public void destroy(String alias) {
            cacheMap.remove(alias);
        }
    }
}
