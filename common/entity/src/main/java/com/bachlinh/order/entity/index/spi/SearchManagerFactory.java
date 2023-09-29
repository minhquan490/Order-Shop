package com.bachlinh.order.entity.index.spi;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import com.bachlinh.order.core.container.DependenciesResolver;

import java.io.IOException;

/**
 * The factory for create {@link SearchManager} with options obtain from the builder.
 *
 * @author Hoang Minh Quan
 */
public interface SearchManagerFactory {

    /**
     * Build {@link SearchManager} with given options.
     *
     * @return {@link SearchManager} ready for use.
     */
    SearchManager obtainManager() throws IOException;

    /**
     * Open directory of mapped entity.
     *
     * @param mappedEntity  Entity type.
     * @param directoryPath Path of index directory.
     * @param indexName     Name of index directory.
     * @return The {@link DirectoryOperation} holding index directory.
     */
    DirectoryOperation openDirectory(Class<?> mappedEntity, String directoryPath, String indexName);

    /**
     * Config {@link org.apache.lucene.index.IndexWriter} with given {@link Analyzer}.
     *
     * @param analyzer Analyzer for factory config index writer.
     * @return Config of index writer.
     */
    IndexWriterConfig configWriter(Analyzer analyzer);

    /**
     * {@link SearchManagerFactory} builder, use for building {@code SearchManagerFactory} with
     * manual options.
     *
     * @author Hoang Minh Quan.
     */
    interface Builder {

        /**
         * Config index file path.
         *
         * @param path Path of index file or path of directory index.
         * @return This builder for continue building.
         */
        Builder indexFilePath(String path);

        /**
         * Config names of index store.
         *
         * @param names Names of index store.
         * @return This builder for continue building.
         */
        Builder indexNames(String... names);

        /**
         * Specify search manager use {@link org.apache.lucene.analysis.standard.StandardAnalyzer} for
         * create {@link IndexWriterConfig}.
         *
         * @param use true if StandardAnalyzer must be applied.
         * @return This builder for continue building.
         */
        Builder useStandardAnalyzer(boolean use);

        /**
         * Config stop word file path for search engine use.
         *
         * @param path Path of stop word file.
         * @return This builder for continue building.
         * @see <a href="https://en.wikipedia.org/wiki/Stop_word">Stop word</a> for detail
         */
        Builder stopWordFilePath(String path);

        /**
         * Types of entity, use for mapping to directory.
         *
         * @param entities Entity types.
         * @return This builder for continue building.
         */
        Builder entities(Class<?>... entities);

        Builder profile(String profile);

        Builder dependenciesResolver(DependenciesResolver resolver);

        /**
         * Build {@link SearchManagerFactory} with builder options.
         *
         * @return Built factory ready for use.
         */
        SearchManagerFactory build();
    }
}
