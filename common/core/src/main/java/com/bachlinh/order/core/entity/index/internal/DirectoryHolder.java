package com.bachlinh.order.core.entity.index.internal;

import com.bachlinh.order.core.entity.index.spi.DirectoryOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Lucene index file directory holder. Hold information of the directory, entity
 * mapped to this directory and path of this directory.
 *
 * @author Hoang Minh Quan.
 */
public class DirectoryHolder implements DirectoryOperation {
    private final Class<?> mappedEntity;
    private final String directoryPath;
    private Directory directory;

    public DirectoryHolder(Class<?> mappedEntity, String directoryPath) {
        this.directoryPath = directoryPath;
        this.mappedEntity = mappedEntity;
    }

    @Override
    public Directory open() throws IOException {
        if (directory == null) {
            directory = new NIOFSDirectory(Path.of(directoryPath));
        }
        return directory;
    }

    @Override
    public void close() throws IOException {
        if (directory != null) {
            directory.close();
        }
        directory = null;
    }

    /**
     * Return the entity type associate with directory.
     *
     * @return Entity type.
     */
    public Class<?> getMappedEntity() {
        return mappedEntity;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }
}
