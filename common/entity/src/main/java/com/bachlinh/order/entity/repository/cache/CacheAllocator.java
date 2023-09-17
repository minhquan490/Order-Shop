package com.bachlinh.order.entity.repository.cache;

public interface CacheAllocator {
    Cache allocate(CacheManager<?> cacheManager, String alias);
}
