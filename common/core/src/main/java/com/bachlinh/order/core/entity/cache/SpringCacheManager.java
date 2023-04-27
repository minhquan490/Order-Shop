package com.bachlinh.order.core.entity.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cache.jcache.JCacheCacheManager;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;

/**
 * Subclass of {@link JCacheCacheManager} for apply default configuration
 * for caching storage.
 *
 * @author Hoang Minh Quan
 */
@Getter
@Setter
public class SpringCacheManager extends JCacheCacheManager {
    private MutableConfiguration<Object, Object> defaultConfig;

    public SpringCacheManager(CacheManager cacheManager) {
        super(cacheManager);
    }
}
