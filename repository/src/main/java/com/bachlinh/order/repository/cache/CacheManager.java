package com.bachlinh.order.repository.cache;

public interface CacheManager<T> {
    T unwrap();

    Cache createCache(String alias);

    Cache getCache(String alias);

    void removeCache(String alias);

    void init();

    void close();


}
