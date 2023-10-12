package com.bachlinh.order.entity.repository.query;

import java.util.Collection;

/**
 * Management of native query built by {@link SqlBuilder}.
 *
 * @author MinhQuan
 * */
public interface NativeQueryHolder {

    /**
     * Return native query that being managed by this object.
     *
     * @return Native managed query.
     * */
    String getNativeQuery();

    /**
     * Return parameters for sql query that being managed by this object.
     *
     * @return Binding parameters of query.
     * */
    Collection<QueryBinding> getQueryBindings();
}
