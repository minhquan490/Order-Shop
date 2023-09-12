package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "CRAWL_RESULT", indexes = @Index(name = "idx_crawl_result_time_finish", columnList = "TIME_FINISH"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
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
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setSourcePath(String sourcePath) {
        if (this.sourcePath != null && !this.sourcePath.equals(sourcePath)) {
            trackUpdatedField("SOURCE_PATH", this.sourcePath, sourcePath);
        }
        this.sourcePath = sourcePath;
    }

    @ActiveReflection
    public void setTimeFinish(Timestamp timeFinish) {
        if (this.timeFinish != null && !this.timeFinish.equals(timeFinish)) {
            trackUpdatedField("TIME_FINISH", this.timeFinish, timeFinish);
        }
        this.timeFinish = timeFinish;
    }

    @ActiveReflection
    public void setResources(String resources) {
        if (this.resources != null && !this.resources.equals(resources)) {
            trackUpdatedField("RESOURCES", this.resources, resources);
        }
        this.resources = resources;
    }
}
