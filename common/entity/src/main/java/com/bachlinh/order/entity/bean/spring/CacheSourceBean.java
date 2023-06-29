package com.bachlinh.order.entity.bean.spring;

import jakarta.persistence.Cacheable;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.cache.SpringCacheManager;
import com.bachlinh.order.exception.system.common.CriticalException;

import java.util.Collection;
import java.util.Optional;

@Configuration
@EnableCaching(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
public class CacheSourceBean {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public SpringCacheManager cacheManager() {
        SpringCacheManager cacheManager = new SpringCacheManager(initCacheManager());
        cacheManager.setAllowNullValues(false);
        cacheManager.setTransactionAware(true);
        cacheManager.setDefaultConfig(defaultConfiguration());
        return cacheManager;
    }


    private Collection<Class<?>> queryCacheStorage() {
        ApplicationScanner scanner = new ApplicationScanner();
        return scanner.findComponents().stream().filter(this::conditionFilter).toList();
    }

    private boolean conditionFilter(Class<?> entity) {
        return entity.isAnnotationPresent(Cacheable.class) &&
                entity.isAnnotationPresent(Cache.class);
    }

    private MutableConfiguration<Object, Object> defaultConfiguration() {
        return new MutableConfiguration<>()
                .setTypes(Object.class, Object.class)
                .setStoreByValue(true)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR));
    }

    private CacheManager initCacheManager() {
        return Optional.of(Caching.getCachingProvider().getCacheManager()).map(cacheManager -> {
                    queryCacheStorage().forEach(cache -> {
                        Cache cacheStorage = cache.getAnnotation(Cache.class);
                        if (cacheManager.getCache(cacheStorage.region()) == null) {
                            if (log.isDebugEnabled()) {
                                log.debug("Create cache storage {}", cacheStorage.region());
                            }
                            cacheManager.createCache(cacheStorage.region(), defaultConfiguration());
                            if (log.isDebugEnabled()) {
                                log.debug("Create done");
                            }
                        }
                    });
                    return cacheManager;
                })
                .orElseThrow(() -> new CriticalException("Can not create CacheManager"));
    }
}
