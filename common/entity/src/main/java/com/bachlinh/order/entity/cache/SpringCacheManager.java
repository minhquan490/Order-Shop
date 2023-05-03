package com.bachlinh.order.entity.cache;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import org.springframework.cache.jcache.JCacheCacheManager;

/**
 * Subclass of {@link JCacheCacheManager} for apply default configuration
 * for caching storage.
 *
 * @author Hoang Minh Quan
 */
public class SpringCacheManager extends JCacheCacheManager {
    private MutableConfiguration<Object, Object> defaultConfig;

    public SpringCacheManager(CacheManager cacheManager) {
        super(cacheManager);
    }

    public MutableConfiguration<Object, Object> getDefaultConfig() {
        return this.defaultConfig;
    }

    public void setDefaultConfig(MutableConfiguration<Object, Object> defaultConfig) {
        this.defaultConfig = defaultConfig;
    }
}
