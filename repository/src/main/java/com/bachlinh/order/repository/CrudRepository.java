package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.repository.query.Where;

import java.util.Collection;

public interface CrudRepository<T, U extends BaseEntity<T>> {
    <S extends U> S save(S entity);

    <S extends U> S update(S entity);

    <S extends U> Collection<S> saveAll(Iterable<S> entities);

    <S extends U> Collection<S> updateAll(Iterable<S> entities);

    boolean exists(T id);

    long count();

    long count(Where... conditions);

    void deleteById(T id);

    void delete(U entity);

    void deleteAllById(Iterable<? extends T> ids);

    void deleteAll(Iterable<? extends U> entities);

    void deleteAll();
}
