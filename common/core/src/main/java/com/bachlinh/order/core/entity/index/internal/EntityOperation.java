package com.bachlinh.order.core.entity.index.internal;

import com.bachlinh.order.core.entity.index.spi.DirectoryOperation;
import com.bachlinh.order.core.entity.index.spi.EntityIndexer;
import com.bachlinh.order.core.entity.index.spi.EntitySearcher;
import com.bachlinh.order.core.entity.index.spi.FieldDescriptor;
import com.bachlinh.order.core.entity.index.spi.FullTextSearchMetadata;
import com.bachlinh.order.core.entity.index.spi.MetadataFactory;
import com.bachlinh.order.core.entity.model.BaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
class EntityOperation implements EntityIndexer, DirectoryOperation, EntitySearcher {
    private final MetadataFactory metadataFactory;
    private final IndexWriterConfig config;
    private final DirectoryHolder directoryHolder;
    private final FieldDescriptor descriptor;
    private IndexWriter indexWriter;

    @Override
    public void index(Object entity, boolean closedHook) {
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
//            if (closedHook) {
//                indexWriter.close();
//            } else {
//                indexWriter.commit();
//            }
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
            case "Integer" -> new IntField(name, (Integer) value);
            case "Float" -> new FloatField(name, (Float) value);
            case "Double" -> new DoubleField(name, (Double) value);
            case "Long" -> new LongField(name, (Long) value);
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
