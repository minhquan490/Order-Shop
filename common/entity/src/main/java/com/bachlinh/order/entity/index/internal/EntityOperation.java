package com.bachlinh.order.entity.index.internal;

import com.bachlinh.order.entity.index.spi.DirectoryOperation;
import com.bachlinh.order.entity.index.spi.EntityIndexer;
import com.bachlinh.order.entity.index.spi.EntitySearcher;
import com.bachlinh.order.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.entity.index.spi.FullTextSearchMetadata;
import com.bachlinh.order.entity.index.spi.MetadataFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class EntityOperation implements EntityIndexer, DirectoryOperation, EntitySearcher {
    private static final String ENTITY_ID_FIELD = "entityId";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final MetadataFactory metadataFactory;
    private final IndexWriterConfig config;
    private final DirectoryHolder directoryHolder;
    private final FieldDescriptor descriptor;
    private IndexWriter indexWriter;

    EntityOperation(MetadataFactory metadataFactory, IndexWriterConfig config, DirectoryHolder directoryHolder, FieldDescriptor descriptor) {
        this.metadataFactory = metadataFactory;
        this.config = config;
        this.directoryHolder = directoryHolder;
        this.descriptor = descriptor;
    }

    @Override
    public void index(Object entity) {
        try {
            initIndex();
            long totalIndexedDoc = addDocument(entity);
            if (totalIndexedDoc > 0) {
                flush();
            }
        } catch (IOException e) {
            log.warn("Can not index entity [{}] because IOException, detail [{}]", entity.getClass().getName(), e.getMessage());
        } finally {
            log.info("Index entity [{}] complete", entity.getClass().getName());
        }
    }

    @Override
    public void indexMany(Collection<Object> entities) {
        Object trackingEntity = entities.stream().findFirst().orElse(null);
        if (trackingEntity == null) {
            return;
        }
        log.info("Index collection entities [{}], total elements: [{}]", trackingEntity.getClass(), entities.size());
        try {
            initIndex();
            long totalIndexedDoc = 0;
            for (Object entity : entities) {
                totalIndexedDoc += addDocument(entity);
            }
            if (totalIndexedDoc > 0) {
                flush();
            }
        } catch (IOException e) {
            log.error("Can not index entities because IOException, detail [{}]", e.getMessage());
        } finally {
            log.info("Index collection entities [{}] complete", trackingEntity.getClass().getName());
        }
    }

    @Override
    public Directory open() throws IOException {
        return directoryHolder.open();
    }

    @Override
    public void close() throws IOException {
        directoryHolder.close();
    }

    @Override
    public Collection<String> search(String keyword) {
        Collection<String> searchFields = descriptor.storeableFields();
        try (DirectoryReader directoryReader = DirectoryReader.open(open())) {
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
            QueryParser parser = new MultiFieldQueryParser(searchFields.toArray(new String[0]), config.getAnalyzer());
            Query query = parser.parse(keyword);
            ScoreDoc[] scoreDoc = indexSearcher.search(query, 10).scoreDocs;
            StoredFields storedFields = indexSearcher.storedFields();
            List<Document> documents = new ArrayList<>();
            for (ScoreDoc value : scoreDoc) {
                documents.add(storedFields.document(value.doc));
            }
            return documents.stream().map(doc -> doc.get(ENTITY_ID_FIELD)).collect(Collectors.toSet());
        } catch (IOException | ParseException e) {
            log.warn("Can not search data for key [{}] because [{}]", keyword, e.getClass().getName());
            return Collections.emptyList();
        }
    }

    void setIndexWriter(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }

    IndexWriter buildIndexWriter() throws IOException {
        IndexWriterConfig configClone = cloneConfig();
        return new IndexWriter(open(), configClone);
    }

    private Field findField(Object value, String name) {
        return switch (value.getClass().getSimpleName()) {
            case "String" -> new TextField(name, (String) value, Field.Store.YES);
            case "Integer" -> new IntField(name, (Integer) value, Field.Store.NO);
            case "Float" -> new FloatField(name, (Float) value, Field.Store.NO);
            case "Double" -> new DoubleField(name, (Double) value, Field.Store.NO);
            case "Long" -> new LongField(name, (Long) value, Field.Store.NO);
            default -> new Field(name, String.valueOf(value), TextField.TYPE_STORED);
        };
    }

    private IndexWriterConfig cloneConfig() {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(config.getAnalyzer());
        if (config.getCodec() != null) {
            indexWriterConfig.setCodec(config.getCodec());
        }
        if (config.getOpenMode() != null) {
            indexWriterConfig.setOpenMode(config.getOpenMode());
        }
        indexWriterConfig.setCommitOnClose(config.getCommitOnClose());
        if (config.getMergePolicy() != null) {
            indexWriterConfig.setMergePolicy(config.getMergePolicy());
        }
        return indexWriterConfig;
    }

    private void flush() throws IOException {
        indexWriter.flush();
        indexWriter.commit();
        log.info("Flush index writer done.");
    }

    private void initIndex() throws IOException {
        if (indexWriter == null || !indexWriter.isOpen()) {
            indexWriter = buildIndexWriter();
        }
    }

    private long addDocument(Object entity) throws IOException {
        FullTextSearchMetadata metadata = metadataFactory.buildFullTextMetadata(entity, descriptor);
        Document document = new Document();
        Set<Map.Entry<String, Object>> fieldValues = metadata.getStoreableFieldValue()
                .entrySet()
                .stream()
                .filter(stringObjectEntry -> {
                    if (stringObjectEntry.getValue() == null) {
                        return false;
                    }
                    if (stringObjectEntry.getValue() instanceof String casted) {
                        // Does not accept empty string
                        return StringUtils.hasText(casted);
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toSet());
        if (fieldValues.size() != metadata.getStoreableFieldValue().size()) {
            // Document scheme require same, so do not index entity when
            // total field values not equal with indexable field values.
            return 0;
        }
        for (Map.Entry<String, Object> value : fieldValues) {
            Field field = findField(value.getValue(), value.getKey());
            document.add(field);
        }
        if (entity instanceof BaseEntity<?> e) {
            Field field = findField(Objects.requireNonNull(e.getId()), ENTITY_ID_FIELD);
            document.add(field);
        }
        indexWriter.addDocument(document);
        return document.getFields().size();
    }
}
