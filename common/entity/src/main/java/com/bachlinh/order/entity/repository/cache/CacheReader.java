package com.bachlinh.order.entity.repository.cache;

public interface CacheReader<T, U> {
    U readCache(String alias, T key);
}
