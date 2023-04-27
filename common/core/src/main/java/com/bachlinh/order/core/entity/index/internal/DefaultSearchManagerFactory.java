package com.bachlinh.order.core.entity.index.internal;

import com.bachlinh.order.analyzer.VietnameseAnalyzer;
import com.bachlinh.order.analyzer.VietnameseConfig;
import com.bachlinh.order.core.entity.index.spi.SearchManager;
import com.bachlinh.order.core.entity.index.spi.SearchManagerFactory;
import com.bachlinh.order.core.entity.index.spi.StopWordLoader;
import com.bachlinh.order.environment.Environment;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.lucene95.Lucene95Codec;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NoMergePolicy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class DefaultSearchManagerFactory implements SearchManagerFactory {
    private final String indexFilePath;
    private final String[] indexNames;
    private final boolean useStandard;
    private final String stopWordPath;
    private final Collection<Class<?>> entities;
    private final ThreadPoolTaskExecutor executor;

    @Override
    public SearchManager obtainManager() throws IOException {
        Collection<String> indexes = Arrays.asList(indexNames);
        Map<Class<?>, DirectoryHolder> directoryHolderMap = entities
                .stream()
                .filter(clazz -> indexes.contains(clazz.getSimpleName().toLowerCase()))
                .collect(Collectors.toMap(entity -> entity, entity -> openDirectory(entity, indexFilePath, findIndexName(entity))));
        if (useStandard) {
            return new SimpleSearchManager(directoryHolderMap, configWriter(new StandardAnalyzer()), executor);
        } else {
            Environment environment = Environment.getInstance("common");
            Analyzer analyzer = new VietnameseAnalyzer(new VietnameseConfig(environment.getProperty("server.tokenizer.path"), new InternalStopWordLoader(stopWordPath).loadStopWord()));
            return new SimpleSearchManager(directoryHolderMap, configWriter(analyzer), executor);
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
            if (indexName.equalsIgnoreCase(entity.getName())) {
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
        private ThreadPoolTaskExecutor executor;

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
        public SearchManagerFactory.Builder threadPool(ThreadPoolTaskExecutor executor) {
            this.executor = executor;
            return this;
        }

        @Override
        public SearchManagerFactory build() {
            return new DefaultSearchManagerFactory(indexFilePath, indexNames, useStandard, stopWordPath, entities, executor);
        }
    }

    private record InternalStopWordLoader(String stopWordPath) implements StopWordLoader {

        @Override
        public CharArraySet loadStopWord() throws IOException {
            if (stopWordPath == null) {
                return CharArraySet.EMPTY_SET;
            }
            if (!stopWordPath.endsWith(".txt")) {
                throw new UnsupportedOperationException("Only support txt file");
            }
            FileChannel channel = openChannel(stopWordPath);
            Collection<Byte> bytes = load(channel, new ArrayList<>());
            return parseBuffer(bytes);
        }

        private FileChannel openChannel(String path) throws IOException {
            File file = new File(path);
            if (!file.exists()) {
                throw new NoSuchElementException("Can find file with path [" + path + "]");
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            return randomAccessFile.getChannel();
        }

        private Collection<Byte> load(FileChannel channel, Collection<Byte> data) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            while (channel.read(buffer) > 0) {
                data.add(buffer.array()[0]);
                load(channel, data);
            }
            return data;
        }

        private CharArraySet parseBuffer(Collection<Byte> buffer) {
            byte[] data = new byte[buffer.size()];
            for (int i = 0; i < buffer.size(); i++) {
                data[i] = ((List<Byte>) buffer).get(i);
            }
            String keyword = new String(data, StandardCharsets.UTF_8);
            buffer.clear();
            String[] keys = keyword.split(System.lineSeparator());
            Set<String> keySet = new HashSet<>();
            for (String key : keys) {
                keySet.addAll(Stream.of(key.split(",")).toList());
            }
            return new CharArraySet(keySet, true);
        }
    }
}
