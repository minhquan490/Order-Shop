package com.bachlinh.order.entity.repository.cache.implementor;

import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.repository.cache.AbstractCacheManager;
import com.bachlinh.order.entity.repository.cache.Cache;
import com.bachlinh.order.entity.repository.cache.CacheAllocator;
import com.bachlinh.order.entity.repository.cache.CacheDestroyer;
import com.bachlinh.order.entity.repository.cache.CacheLoader;
import com.bachlinh.order.entity.repository.cache.CacheManager;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EhDiskCacheManager extends AbstractCacheManager<PersistentCacheManager> {

    private static PersistentCacheManager persistentCacheManager;
    private static Context context;

    static {
        context = new Context();
    }

    public EhDiskCacheManager(String storagePath) {
        super(configCacheManager(storagePath), context, context, context);
        init();
    }

    @Override
    protected void internalWrite(String key, Object value) {
        String alias = QueryUtils.parseQueryToEntitySimpleName(key);
        Cache cache = getCache(alias);
        cache.put(() -> key, () -> value);
    }

    @Override
    public void init() {
        persistentCacheManager.init();
    }

    @Override
    public void close() {
        persistentCacheManager.close();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<AbstractEntity<?>> readCache(String alias, String key) {
        Cache cache = context.load(alias);
        return (Collection<AbstractEntity<?>>) cache.get(() -> key).unwrap();
    }

    private static PersistentCacheManager configCacheManager(String storagePath) {
        persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(configStorage(storagePath)))
                .build();
        return persistentCacheManager;
    }

    private static File configStorage(String path) {
        return new File(path);
    }

    private static class Context implements CacheAllocator, CacheLoader, CacheDestroyer {

        private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

        @Override
        public Cache allocate(CacheManager<?> cacheManager, String alias) {
            Cache cache = new EhDiskCache(alias, (org.ehcache.CacheManager) cacheManager.unwrap());
            return cacheMap.put(alias, cache);
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
