package com.bachlinh.order.entity.repository.cache;

public interface CacheWriter<T> {
    void write(T key, Object value);
}