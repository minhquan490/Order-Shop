package com.bachlinh.order.repository.cache;

public interface CacheAllocator {
    Cache allocate(CacheManager<?> cacheManager, String alias);
}
