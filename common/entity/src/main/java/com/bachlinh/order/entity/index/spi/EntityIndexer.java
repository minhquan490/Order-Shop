package com.bachlinh.order.entity.index.spi;

import java.util.Collection;

/**
 * The indexer will index entity field for search engine use.
 *
 * @author Hoang Minh Quan
 */
public interface EntityIndexer {

    void index(Object entity);

    void indexMany(Collection<Object> entities);
}
