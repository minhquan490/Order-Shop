package com.bachlinh.order.entity.index.spi;

/**
 * The indexer will index entity field for search engine use.
 *
 * @author Hoang Minh Quan
 */
public interface EntityIndexer {

    void index(Object entity);
}
