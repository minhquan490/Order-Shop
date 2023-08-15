package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Queue;

@Entity
@Table(name = "CRAWL_RESULT", indexes = @Index(name = "idx_crawl_result_time_finish", columnList = "TIME_FINISH"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class CrawlResult extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", unique = true, updatable = false)
    private Integer id;

    @Column(name = "SOURCE_PATH", columnDefinition = "nvarchar(100)", updatable = false, nullable = false)
    private String sourcePath;

    @Column(name = "TIME_FINISH", updatable = false, nullable = false)
    private Timestamp timeFinish = Timestamp.from(Instant.now());

    @Column(name = "RESOURCES", columnDefinition = "nvarchar(1000)", updatable = false)
    private String resources;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of CrawlResult must be int");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setSourcePath(String sourcePath) {
        if (this.sourcePath != null && !this.sourcePath.equals(sourcePath)) {
            trackUpdatedField("SOURCE_PATH", this.sourcePath);
        }
        this.sourcePath = sourcePath;
    }

    @ActiveReflection
    public void setTimeFinish(Timestamp timeFinish) {
        if (this.timeFinish != null && !this.timeFinish.equals(timeFinish)) {
            trackUpdatedField("TIME_FINISH", this.timeFinish.toString());
        }
        this.timeFinish = timeFinish;
    }

    @ActiveReflection
    public void setResources(String resources) {
        if (this.resources != null && !this.resources.equals(resources)) {
            trackUpdatedField("RESOURCES", this.resources);
        }
        this.resources = resources;
    }

    public static EntityMapper<CrawlResult> getMapper() {
        return new CrawlResultMapper();
    }

    private static class CrawlResultMapper implements EntityMapper<CrawlResult> {

        @Override
        public CrawlResult map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new CrawlResult().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public CrawlResult map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            CrawlResult result = new CrawlResult();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("CRAWL_RESULT")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            return result;
        }

        private void setData(CrawlResult target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "CRAWL_RESULT.ID" -> target.setId(mappingObject.value());
                case "CRAWL_RESULT.SOURCE_PATH" -> target.setSourcePath((String) mappingObject.value());
                case "CRAWL_RESULT.TIME_FINISH" -> target.setTimeFinish((Timestamp) mappingObject.value());
                case "CRAWL_RESULT.RESOURCES" -> target.setResources((String) mappingObject.value());
                case "CRAWL_RESULT.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "CRAWL_RESULT.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "CRAWL_RESULT.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "CRAWL_RESULT.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
