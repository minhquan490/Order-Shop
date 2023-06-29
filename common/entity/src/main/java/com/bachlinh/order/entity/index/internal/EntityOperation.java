package com.bachlinh.order.entity.index.internal;

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
import com.bachlinh.order.entity.index.spi.DirectoryOperation;
import com.bachlinh.order.entity.index.spi.EntityIndexer;
import com.bachlinh.order.entity.index.spi.EntitySearcher;
import com.bachlinh.order.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.entity.index.spi.FullTextSearchMetadata;
import com.bachlinh.order.entity.index.spi.MetadataFactory;
import com.bachlinh.order.entity.model.BaseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class EntityOperation implements EntityIndexer, DirectoryOperation, EntitySearcher {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final MetadataFactory metadataFactory;
    private final IndexWriterConfig config;
    private final DirectoryHolder directoryHolder;
    private final FieldDescriptor descriptor;
    private IndexWriter indexWriter;

    public EntityOperation(MetadataFactory metadataFactory, IndexWriterConfig config, DirectoryHolder directoryHolder, FieldDescriptor descriptor) {
        this.metadataFactory = metadataFactory;
        this.config = config;
        this.directoryHolder = directoryHolder;
        this.descriptor = descriptor;
    }

    @Override
    public void index(Object entity) {
        try {
            if (indexWriter == null || !indexWriter.isOpen()) {
                indexWriter = buildIndexWriter();
            }
            FullTextSearchMetadata metadata = metadataFactory.buildFullTextMetadata(entity, descriptor);
            Document document = new Document();
            Set<Map.Entry<String, Object>> fieldValues = metadata.getStoreableFieldValue().entrySet();
            for (Map.Entry<String, Object> value : fieldValues) {
                Field field = findField(value.getValue(), value.getKey());
                document.add(field);
            }
            if (entity instanceof BaseEntity e) {
                document.add(findField(e.getId(), "id"));
            }
            indexWriter.addDocument(document);
            indexWriter.commit();
        } catch (IOException e) {
            log.warn("Can not index entity [{}] because IOException, detail [{}]", entity.getClass().getName(), e.getMessage());
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
            return documents.stream().map(doc -> doc.get("id")).collect(Collectors.toSet());
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
}
