package com.bachlinh.order.repository.cache;

public interface CacheWriter<T> {
    void write(T key, Object value);
}