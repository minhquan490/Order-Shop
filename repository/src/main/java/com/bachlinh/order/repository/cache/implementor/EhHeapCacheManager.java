package com.bachlinh.order.repository.cache.implementor;

import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.cache.AbstractCacheContext;
import com.bachlinh.order.repository.cache.AbstractCacheManager;
import com.bachlinh.order.repository.cache.Cache;
import com.bachlinh.order.repository.utils.QueryUtils;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;

import java.util.Collection;
import java.util.Collections;

public class EhHeapCacheManager extends AbstractCacheManager<CacheManager> {
    private static final Context CONTEXT;

    static {
        CONTEXT = new Context();
    }

    public EhHeapCacheManager() {
        super(configCacheManager(), CONTEXT, CONTEXT, CONTEXT);
        init();
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
        Cache cache = CONTEXT.load(alias);
        if (cache == null) {
            return Collections.emptyList();
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
        return CacheManagerBuilder.newCacheManagerBuilder()
                .build();
    }

    private static class Context extends AbstractCacheContext {

        @Override
        protected Cache doCreateCache(String alias, CacheManager cacheManager) {
            return new EhHeapCache(alias, cacheManager);
        }
    }
}
