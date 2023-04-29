package com.bachlinh.order.service;

public interface BaseService<T, U> {
    Result<T> save(Form<U> form);

    Result<T> delete(Form<U> form);

    Result<T> update(Form<U> form);

    Result<T> getOne(Form<U> form);

    <X extends Iterable<T>> Result<X> getList(Form<U> form);
}
