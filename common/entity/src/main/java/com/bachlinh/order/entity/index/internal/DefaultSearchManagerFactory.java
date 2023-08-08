package com.bachlinh.order.entity.index.internal;

import com.bachlinh.order.analyzer.StopWordLoader;
import com.bachlinh.order.analyzer.VietnameseAnalyzer;
import com.bachlinh.order.analyzer.VietnameseConfig;
import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.index.spi.SearchManagerFactory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.lucene95.Lucene95Codec;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NoMergePolicy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class DefaultSearchManagerFactory implements SearchManagerFactory {
    private final String indexFilePath;
    private final String[] indexNames;
    private final boolean useStandard;
    private final String stopWordPath;
    private final Collection<Class<?>> entities;
    private final EntityProxyFactory entityProxyFactory;
    private final String profile;

    public DefaultSearchManagerFactory(String indexFilePath, String[] indexNames, boolean useStandard, String stopWordPath, Collection<Class<?>> entities, DependenciesResolver dependenciesResolver, String profile) {
        this.indexFilePath = indexFilePath;
        this.indexNames = indexNames;
        this.useStandard = useStandard;
        this.stopWordPath = stopWordPath;
        this.entities = entities;
        this.entityProxyFactory = dependenciesResolver.resolveDependencies(EntityProxyFactory.class);
        this.profile = profile;
    }

    @Override
    public SearchManager obtainManager() throws IOException {
        Collection<String> indexes = Arrays.asList(indexNames);
        Map<Class<?>, DirectoryHolder> directoryHolderMap = entities
                .stream()
                .filter(clazz -> indexes.contains(clazz.getSimpleName().toLowerCase()))
                .collect(Collectors.toMap(entity -> entity, entity -> openDirectory(entity, indexFilePath, findIndexName(entity))));
        if (useStandard) {
            return new SimpleSearchManager(directoryHolderMap, configWriter(new StandardAnalyzer()), entityProxyFactory);
        } else {
            Environment environment = Environment.getInstance(profile);
            Analyzer analyzer = new VietnameseAnalyzer(new VietnameseConfig(environment.getProperty("server.tokenizer.path"), StopWordLoader.defaultLoader(stopWordPath).loadStopWord()));
            return new SimpleSearchManager(directoryHolderMap, configWriter(analyzer), entityProxyFactory);
        }
    }

    @Override
    public DirectoryHolder openDirectory(Class<?> mappedEntity, String directoryPath, String indexName) {
        return new DirectoryHolder(mappedEntity, String.join("/", directoryPath, indexName));
    }

    @Override
    public IndexWriterConfig configWriter(Analyzer analyzer) {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setCommitOnClose(true);
        config.setMergePolicy(NoMergePolicy.INSTANCE);
        config.setCodec(new Lucene95Codec(Lucene95Codec.Mode.BEST_SPEED));
        return config;
    }

    static SearchManagerFactory.Builder builder() {
        return new Builder();
    }

    private String findIndexName(Class<?> entity) {
        for (String indexName : indexNames) {
            if (indexName.equalsIgnoreCase(entity.getSimpleName())) {
                return indexName;
            }
        }
        return "";
    }

    private static class Builder implements SearchManagerFactory.Builder {
        private String indexFilePath;
        private String[] indexNames;
        private boolean useStandard = false;
        private String stopWordPath;
        private Collection<Class<?>> entities;
        private DependenciesResolver resolver;
        private String profile;

        @Override
        public SearchManagerFactory.Builder indexFilePath(String path) {
            this.indexFilePath = path;
            return this;
        }

        @Override
        public SearchManagerFactory.Builder indexNames(String... names) {
            indexNames = names;
            return this;
        }

        @Override
        public SearchManagerFactory.Builder useStandardAnalyzer(boolean use) {
            this.useStandard = use;
            return this;
        }

        @Override
        public SearchManagerFactory.Builder stopWordFilePath(String path) {
            this.stopWordPath = path;
            return this;
        }

        @Override
        public SearchManagerFactory.Builder entities(Class<?>... entities) {
            this.entities = Arrays.asList(entities);
            return this;
        }

        @Override
        public SearchManagerFactory.Builder profile(String profile) {
            this.profile = Objects.requireNonNull(profile, "Profile must not be null");
            return this;
        }

        @Override
        public SearchManagerFactory.Builder dependenciesResolver(DependenciesResolver resolver) {
            this.resolver = resolver;
            return this;
        }

        @Override
        public SearchManagerFactory build() {
            Objects.requireNonNull(profile, "Profile must be specific");
            Objects.requireNonNull(resolver, "Dependencies resolver must be specific");
            return new DefaultSearchManagerFactory(indexFilePath, indexNames, useStandard, stopWordPath, entities, resolver, profile);
        }
    }
}
