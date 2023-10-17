package com.bachlinh.order.repository.query;

public interface SqlLimitOffset<T> {
    T limit(long limit);

    T offset(long offset);
}
