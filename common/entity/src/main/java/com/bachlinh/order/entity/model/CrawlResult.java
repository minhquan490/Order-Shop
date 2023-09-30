package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "CRAWL_RESULT", indexes = @Index(name = "idx_crawl_result_time_finish", columnList = "TIME_FINISH"))
@ActiveReflection
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

    @ActiveReflection
    protected CrawlResult() {
    }

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

    public Integer getId() {
        return this.id;
    }

    public String getSourcePath() {
        return this.sourcePath;
    }

    public Timestamp getTimeFinish() {
        return this.timeFinish;
    }

    public String getResources() {
        return this.resources;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CrawlResult other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$sourcePath = this.getSourcePath();
        final Object other$sourcePath = other.getSourcePath();
        if (!Objects.equals(this$sourcePath, other$sourcePath))
            return false;
        final Object this$timeFinish = this.getTimeFinish();
        final Object other$timeFinish = other.getTimeFinish();
        if (!Objects.equals(this$timeFinish, other$timeFinish))
            return false;
        final Object this$resources = this.getResources();
        final Object other$resources = other.getResources();
        return Objects.equals(this$resources, other$resources);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CrawlResult;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $sourcePath = this.getSourcePath();
        result = result * PRIME + ($sourcePath == null ? 43 : $sourcePath.hashCode());
        final Object $timeFinish = this.getTimeFinish();
        result = result * PRIME + ($timeFinish == null ? 43 : $timeFinish.hashCode());
        final Object $resources = this.getResources();
        result = result * PRIME + ($resources == null ? 43 : $resources.hashCode());
        return result;
    }
}
