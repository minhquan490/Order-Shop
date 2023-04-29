package com.bachlinh.order.entity.index.spi;

import java.util.Map;

/**
 * Metadata of object will be applied full text search on.
 *
 * @author Hoang Minh Quan
 */
public interface FullTextSearchMetadata {

    /**
     * Return the object type.
     *
     * @return Type of object.
     */
    Class<?> getEntityType();

    /**
     * Return field name that will be applied full text search in and this value.
     *
     * @return Field name and it values.
     */
    Map<String, Object> getStoreableFieldValue();
}
