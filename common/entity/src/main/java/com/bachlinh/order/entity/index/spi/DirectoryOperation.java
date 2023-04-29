package com.bachlinh.order.entity.index.spi;

import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * The operation execute on lucene index file directory.
 *
 * @author Hoang Minh Quan.
 */
public interface DirectoryOperation {

    /**
     * Open the lucene index file directory.
     *
     * @throws IOException If problem occur when open directory.
     */
    Directory open() throws IOException;

    /**
     * Close the lucene index file directory.
     *
     * @throws IOException If problem occur when close directory.
     */
    void close() throws IOException;
}
