package com.bachlinh.order.repository.cache.implementor;

import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.cache.AbstractCacheContext;
import com.bachlinh.order.repository.cache.AbstractCacheManager;
import com.bachlinh.order.repository.cache.Cache;
import com.bachlinh.order.repository.utils.QueryUtils;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;

import java.io.File;
import java.util.Collection;

public class EhDiskCacheManager extends AbstractCacheManager<PersistentCacheManager> {
    private static final Context context;

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
        unwrap().init();
    }

    @Override
    public void close() {
        unwrap().close();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<AbstractEntity<?>> readCache(String alias, String key) {
        Cache cache = context.load(alias);
        return (Collection<AbstractEntity<?>>) cache.get(() -> key).unwrap();
    }

    private static PersistentCacheManager configCacheManager(String storagePath) {
        return CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(configStorage(storagePath)))
                .build();
    }

    private static File configStorage(String path) {
        return new File(path);
    }

    private static class Context extends AbstractCacheContext {

        @Override
        protected Cache doCreateCache(String alias, org.ehcache.CacheManager cacheManager) {
            return new EhDiskCache(alias, cacheManager);
        }
    }
}
