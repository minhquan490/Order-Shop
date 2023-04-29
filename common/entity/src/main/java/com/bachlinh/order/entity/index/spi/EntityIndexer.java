package com.bachlinh.order.entity.index.spi;

/**
 * The indexer will index entity field for search engine use.
 *
 * @author Hoang Minh Quan
 */
public interface EntityIndexer {

    /**
     * Index the entity to file local.
     *
     * @param entity     Entity for index.
     * @param closedHook The hook close, true if you to close {@link org.apache.lucene.index.IndexWriter} otherwise false.
     */
    void index(Object entity, boolean closedHook);
}
