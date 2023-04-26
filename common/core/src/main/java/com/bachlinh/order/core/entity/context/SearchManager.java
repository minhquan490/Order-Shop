package com.bachlinh.order.core.entity.context;

import org.apache.lucene.store.Directory;

import java.util.Collection;

/**
 * The manager for manage search and analyze operation on entity.
 *
 * @author Hoang Minh Quan.
 */
public interface SearchManager {

    /**
     * Return index directory of entity.
     *
     * @param entity Entity type for manager find directory of it.
     * @return Directory of entity.
     */
    Directory getDirectory(Class<?> entity);

    /**
     * Analyze the entity and full text search field of it.
     *
     * @param entity     The entity for search engine analyze.
     * @param closedHook Hook for close {@link org.apache.lucene.index.IndexWriter}
     */
    void analyze(Object entity, boolean closedHook);

    /**
     * Analyze all entity in collection.
     *
     * @param entities Entity collection for analyze.
     */
    void analyze(Collection<Object> entities);

    /**
     * Search keyword for specific entity.
     *
     * @param entity  Type of entity for searching.
     * @param keyword The searching keyword.
     * @return Collection entity ids for query database.
     */
    Collection<String> search(Class<?> entity, String keyword);
}
