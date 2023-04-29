package com.bachlinh.order.entity.index.spi;

/**
 * The factory metadata for create {@link FullTextSearchMetadata}.
 *
 * @author Hoang Minh Quan
 */
public interface MetadataFactory {

    /**
     * Build {@link FullTextSearchMetadata} with given entity and field descriptor.
     *
     * @param actualEntity The entity for factory extract field on.
     * @param descriptor   The field descriptor for factory use.
     * @return Metadata of full text search field.
     */
    FullTextSearchMetadata buildFullTextMetadata(Object actualEntity, FieldDescriptor descriptor);
}