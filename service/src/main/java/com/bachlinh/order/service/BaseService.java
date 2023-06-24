package com.bachlinh.order.service;

/**
 * @deprecated will be remove in next version
 */
@Deprecated
public interface BaseService<T, U> {

    /**
     * @deprecated will be remove in next version
     */
    @Deprecated(forRemoval = true, since = "1.0.0")
    Result<T> save(Form<U> form);

    /**
     * @deprecated will be remove in next version
     */
    @Deprecated(forRemoval = true, since = "1.0.0")
    Result<T> delete(Form<U> form);

    /**
     * @deprecated will be remove in next version
     */
    @Deprecated(forRemoval = true, since = "1.0.0")
    Result<T> update(Form<U> form);

    /**
     * @deprecated will be remove in next version
     */
    @Deprecated(forRemoval = true, since = "1.0.0")
    Result<T> getOne(Form<U> form);

    /**
     * @deprecated will be remove in next version
     */
    @Deprecated(forRemoval = true, since = "1.0.0")
    <X extends Iterable<T>> Result<X> getList(Form<U> form);
}
