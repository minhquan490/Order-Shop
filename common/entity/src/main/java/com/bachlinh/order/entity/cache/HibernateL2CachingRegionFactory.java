package com.bachlinh.order.entity.cache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.springframework.lang.Nullable;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Map;

/**
 * Subclass of {@link JCacheRegionFactory} for force create the cache storage and return
 * the custom cache manager.
 *
 * @author Hoang Minh Quan.
 */
public final class HibernateL2CachingRegionFactory extends JCacheRegionFactory {
    private final transient SpringCacheManager cacheManager;

    public HibernateL2CachingRegionFactory(DependenciesResolver dependenciesResolver) {
        this.cacheManager = dependenciesResolver.resolveDependencies(SpringCacheManager.class);
    }

    @Override
    protected CacheManager resolveCacheManager(@Nullable SessionFactoryOptions settings, @Nullable Map<String, Object> properties) {
        return cacheManager.getCacheManager();
    }

    @Override
    protected Cache<Object, Object> createCache(String regionName) {
        Cache<Object, Object> cache = getCacheManager().getCache(regionName);
        if (cache == null) {
            cache = getCacheManager().createCache(regionName, cacheManager.getDefaultConfig());
        }
        return cache;
    }
}
