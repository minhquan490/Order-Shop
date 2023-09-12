package com.bachlinh.order.web.configuration;

import com.bachlinh.order.repository.cache.CacheManager;
import com.bachlinh.order.repository.cache.implementor.EhHeapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager<?> cacheManager() {
        return new EhHeapCacheManager();
    }
}
