package com.bachlinh.order.repository.cache;

public interface CacheReader<T, U> {
    U readCache(String alias, T key);
}
